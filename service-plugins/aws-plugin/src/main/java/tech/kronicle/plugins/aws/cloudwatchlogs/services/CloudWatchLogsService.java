package tech.kronicle.plugins.aws.cloudwatchlogs.services;

import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;
import tech.kronicle.plugins.aws.cloudwatchlogs.client.CloudWatchLogsClientFacade;
import tech.kronicle.plugins.aws.cloudwatchlogs.client.CloudWatchLogsClientFacadeFactory;
import tech.kronicle.plugins.aws.cloudwatchlogs.constants.CloudWatchQueryStatuses;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResult;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResultField;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResults;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.constants.ResourceTypes;
import tech.kronicle.plugins.aws.guice.CloudWatchLogsGetQueryResultsRetry;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.services.ResourceFetcher;
import tech.kronicle.sdk.models.Alias;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentStateLogLevel;
import tech.kronicle.sdk.models.ComponentStateLogMessage;
import tech.kronicle.sdk.models.ComponentStateLogSummary;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static tech.kronicle.plugins.aws.resourcegroupstaggingapi.utils.ResourceUtils.getResourceTagValue;
import static tech.kronicle.plugins.aws.utils.ArnAnalyser.analyseArn;
import static tech.kronicle.plugins.aws.utils.ProfileUtils.processProfilesToMap;

public class CloudWatchLogsService {

    private final CloudWatchLogsClientFacadeFactory clientFacadeFactory;
    private final ResourceFetcher resourceFetcher;
    private final Clock clock;
    private final Retry retry;
    private final AwsConfig config;
    private final String componentTagKey;
    private final String logLevelField;
    private final String logMessageField;
    private Map<AwsProfileAndRegion, Map<String, List<String>>> logGroupNamesByProfileAndRegion;

    @Inject
    public CloudWatchLogsService(
            CloudWatchLogsClientFacadeFactory clientFacadeFactory,
            ResourceFetcher resourceFetcher,
            Clock clock,
            @CloudWatchLogsGetQueryResultsRetry Retry retry,
            AwsConfig config
    ) {
        this.clientFacadeFactory = clientFacadeFactory;
        this.resourceFetcher = resourceFetcher;
        this.clock = clock;
        this.retry = retry;
        this.config = config;
        componentTagKey = config.getTagKeys().getComponent();
        logLevelField = config.getLogFields().getLevel();
        logMessageField = config.getLogFields().getMessage();
    }

    public void refresh() {
        logGroupNamesByProfileAndRegion = processProfilesToMap(config.getProfiles(), this::getLogGroupNamesForProfileAndRegion);
    }

    private Map<String, List<String>> getLogGroupNamesForProfileAndRegion(AwsProfileAndRegion profileAndRegion) {
        return mapLogGroups(resourceFetcher.getResources(
                profileAndRegion,
                List.of(ResourceTypes.LOGS_LOG_GROUP),
                Map.ofEntries(Map.entry(componentTagKey, List.of()))
        ));
    }

    private Map<String, List<String>> mapLogGroups(List<ResourceGroupsTaggingApiResource> resources) {
        return resources.stream()
                .map(resource -> Map.entry(
                        getResourceTagValue(resource, componentTagKey),
                        analyseArn(resource.getArn()).getResourceId()
                ))
                .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())));
    }

    @SneakyThrows
    public Map<AwsProfileAndRegion, List<ComponentStateLogSummary>> getLogSummariesForComponent(Component component) {
        return processProfilesToMap(
                config.getProfiles(),
                getLogSummariesProfileAndRegionAndComponent(component)
        );
    }

    private Function<AwsProfileAndRegion, List<ComponentStateLogSummary>> getLogSummariesProfileAndRegionAndComponent(
            Component component
    ) {
        return profileAndRegion -> {
            GetLogSummary getLogSummary = (
                    String name,
                    String comparisonName,
                    Duration duration,
                    Duration offset
            ) -> {
                ComponentStateLogSummary logSummary = getLogSummary(
                        profileAndRegion,
                        component,
                        name,
                        duration,
                        Duration.ZERO
                );
                if (isNull(logSummary)) {
                    return null;
                }
                return logSummary.withComparison(
                        getLogSummary(
                                profileAndRegion,
                                component,
                                comparisonName,
                                duration,
                                offset
                        )
                );
            };
            return Stream
                    .of(
                            getLogSummary.apply(
                                    "Last hour",
                                    "Previous hour",
                                    Duration.ofHours(1),
                                    Duration.ofHours(1)
                            ),
                            getLogSummary.apply(
                                    "Last 24 hours",
                                    "Previous 24 hours",
                                    Duration.ofDays(1),
                                    Duration.ofDays(1)
                            ),
                            getLogSummary.apply(
                                    "Last 7 days",
                                    "Previous 7 days",
                                    Duration.ofDays(7),
                                    Duration.ofDays(7)
                            )
                    )
                    .filter(Objects::nonNull)
                    .collect(toList());
        };
    }

    private ComponentStateLogSummary getLogSummary(
            AwsProfileAndRegion profileAndRegion,
            Component component,
            String name,
            Duration duration,
            Duration offset
    ) {
        ZonedDateTime now = ZonedDateTime.now(clock);
        Instant endTime = now.toInstant().minus(offset);
        Instant startTime = endTime.minus(duration);
        List<ComponentStateLogLevel> levels = getLevels(profileAndRegion, component, startTime, endTime);
        if (levels.isEmpty()) {
            return null;
        }
        return ComponentStateLogSummary.builder()
                .name(name)
                .levels(levels)
                .updateTimestamp(LocalDateTime.from(now))
                .build();
    }

    private List<ComponentStateLogLevel> getLevels(
            AwsProfileAndRegion profileAndRegion,
            Component component,
            Instant startTime,
            Instant endTime
    ) {
        List<ComponentStateLogLevel> levels = mapMessageCountResults(
                executeMessageCountQuery(
                        component,
                        profileAndRegion,
                        startTime,
                        endTime
                )
        );
        return levels.stream()
                .map(level -> level.withTopMessages(mapTopMessageResults(
                        executeTopMessageQuery(
                                component,
                                profileAndRegion,
                                level,
                                startTime,
                                endTime
                        )
                )))
                .collect(toList());
    }

    private CloudWatchLogsQueryResults executeMessageCountQuery(
            Component component,
            AwsProfileAndRegion profileAndRegion,
            Instant startTime,
            Instant endTime
    ) {
        return executeQuery(
                profileAndRegion,
                getLogGroupNamesForComponent(profileAndRegion, component),
                startTime,
                endTime,
                "stats count(*) as message_count by " + logLevelField + "\n" +
                        "| sort message_count desc\n" +
                        "| limit 10"
        );
    }

    private CloudWatchLogsQueryResults executeTopMessageQuery(
            Component component,
            AwsProfileAndRegion profileAndRegion,
            ComponentStateLogLevel level,
            Instant startTime,
            Instant endTime
    ) {
        return executeQuery(
                profileAndRegion,
                getLogGroupNamesForComponent(profileAndRegion, component),
                startTime,
                endTime,
                "filter " + logLevelField + " = '" + level.getLevel() + "'\n" +
                        "| stats count(*) as message_count by " + logMessageField + " as message\n" +
                        "| sort message_count desc\n" +
                        "| limit 10"
        );
    }

    private List<String> getLogGroupNamesForComponent(AwsProfileAndRegion profileAndRegion, Component component) {
        Map<String, List<String>> logGroupNamesByComponent = logGroupNamesByProfileAndRegion.get(profileAndRegion);
        List<String> logGroupNames = logGroupNamesByComponent.get(component.getId());
        if (nonNull(logGroupNames)) {
            return logGroupNames;
        }
        return component.getAliases().stream()
                .map(Alias::getId)
                .map(logGroupNamesByComponent::get)
                .filter(Objects::nonNull)
                .findFirst().orElse(List.of());
    }

    @SneakyThrows
    private CloudWatchLogsQueryResults executeQuery(
            AwsProfileAndRegion profileAndRegion,
            List<String> logGroupNames,
            Instant startTime,
            Instant endTime,
            String query
    ) {
        if (logGroupNames.isEmpty()) {
            return CloudWatchLogsQueryResults.EMPTY;
        }
        CloudWatchLogsClientFacade clientFacade = clientFacadeFactory.createCloudWatchLogsClientFacade(
                profileAndRegion
        );
        String queryId = clientFacade.startQuery(
                startTime.getEpochSecond(),
                endTime.getEpochSecond(),
                logGroupNames,
                query
        );

        return retry.executeCallable(() -> {
            CloudWatchLogsQueryResults results = clientFacade.getQueryResults(queryId);

            switch (results.getStatus()) {
                case CloudWatchQueryStatuses.SCHEDULED:
                    throw new RuntimeException("Query had not started yet");
                case CloudWatchQueryStatuses.RUNNING:
                    throw new RuntimeException("Query is still running");
                case CloudWatchQueryStatuses.COMPLETE:
                    return results;
                default:
                    throw new RuntimeException("Query has failed with status " + results.getStatus());
            }
        });
    }

    private List<ComponentStateLogLevel> mapMessageCountResults(
            CloudWatchLogsQueryResults results
    ) {
        return results.getResults().stream()
                .map(this::mapMessageCountResult)
                .collect(toList());
    }

    private ComponentStateLogLevel mapMessageCountResult(CloudWatchLogsQueryResult result) {
        return ComponentStateLogLevel.builder()
                .level(getResultFieldValue(result, "level"))
                .count(Long.parseLong(getResultFieldValue(result, "message_count")))
                .build();
    }

    private List<ComponentStateLogMessage> mapTopMessageResults(CloudWatchLogsQueryResults results) {
        return results.getResults().stream()
                .map(this::mapTopMessageResult)
                .collect(toList());
    }

    private ComponentStateLogMessage mapTopMessageResult(CloudWatchLogsQueryResult result) {
        return ComponentStateLogMessage.builder()
                .message(getResultFieldValue(result, "message"))
                .count(Long.parseLong(getResultFieldValue(result, "message_count")))
                .build();
    }

    private String getResultFieldValue(CloudWatchLogsQueryResult result, String name) {
        return result.getFields().stream()
                .filter(field -> Objects.equals(field.getField(), name))
                .findFirst()
                .map(CloudWatchLogsQueryResultField::getValue)
                .orElse(null);
    }

    @FunctionalInterface
    private interface GetLogSummary {

        ComponentStateLogSummary apply(
                String name,
                String comparisonName,
                Duration duration,
                Duration offset
        );
    }
}
