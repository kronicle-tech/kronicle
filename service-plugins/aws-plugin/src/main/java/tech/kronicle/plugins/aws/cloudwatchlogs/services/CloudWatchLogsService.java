package tech.kronicle.plugins.aws.cloudwatchlogs.services;

import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;
import tech.kronicle.plugins.aws.cloudwatchlogs.client.CloudWatchLogsClientFacade;
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
import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.plugins.aws.resourcegroupstaggingapi.utils.ResourceUtils.getResourceTagValue;
import static tech.kronicle.plugins.aws.utils.ArnAnalyser.analyseArn;
import static tech.kronicle.plugins.aws.utils.ProfileUtils.processProfilesToMapEntryList;

public class CloudWatchLogsService {

    private final CloudWatchLogsClientFacade clientFacade;
    private final ResourceFetcher resourceFetcher;
    private final Clock clock;
    private final Retry retry;
    private final AwsConfig config;
    private final String componentTagKey;
    private final String logLevelField;
    private final String logMessageField;
    private List<Map.Entry<AwsProfileAndRegion, Map<String, List<String>>>> logGroupNamesByProfileAndRegion;

    @Inject
    public CloudWatchLogsService(
            CloudWatchLogsClientFacade clientFacade,
            ResourceFetcher resourceFetcher,
            Clock clock,
            @CloudWatchLogsGetQueryResultsRetry Retry retry,
            AwsConfig config
    ) {
        this.clientFacade = clientFacade;
        this.resourceFetcher = resourceFetcher;
        this.clock = clock;
        this.retry = retry;
        this.config = config;
        componentTagKey = config.getTagKeys().getComponent();
        logLevelField = config.getLogFields().getLevel();
        logMessageField = config.getLogFields().getMessage();
    }

    public void refresh() {
        logGroupNamesByProfileAndRegion = processProfilesToMapEntryList(
                config.getProfiles(),
                this::prepareLogGroupNamesForProfileAndRegion
        );
    }

    private Map<String, List<String>> prepareLogGroupNamesForProfileAndRegion(AwsProfileAndRegion profileAndRegion) {
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
    public List<Map.Entry<AwsProfileAndRegion, List<ComponentStateLogSummary>>> getLogSummariesForComponent(Component component) {
        return processProfilesToMapEntryList(
                config.getProfiles(),
                getLogSummariesProfileAndRegionAndComponent(component)
        );
    }

    private Function<AwsProfileAndRegion, List<ComponentStateLogSummary>> getLogSummariesProfileAndRegionAndComponent(
            Component component
    ) {
        return profileAndRegion -> {
            List<String> logGroupNames = getLogGroupNamesForProfileAndRegionAndComponent(
                    profileAndRegion,
                    component
            );
            GetLogSummary getLogSummary = (
                    String name,
                    Duration duration,
                    String comparisonName1,
                    Duration offset1,
                    String comparisonName2,
                    Duration offset2
            ) -> {
                ComponentStateLogSummary logSummary = getLogSummary(
                        profileAndRegion,
                        logGroupNames,
                        name,
                        duration,
                        Duration.ZERO
                );
                if (isNull(logSummary)) {
                    return null;
                }
                return logSummary.withComparisons(filterNotNull(
                        getLogSummary(
                                profileAndRegion,
                                logGroupNames,
                                comparisonName1,
                                duration,
                                offset1
                        ),
                        getLogSummary(
                                profileAndRegion,
                                logGroupNames,
                                comparisonName2,
                                duration,
                                offset2
                        )
                ));
            };
            return filterNotNull(
                    getLogSummary.apply(
                            "Last hour",
                            Duration.ofHours(1),
                            "Previous hour",
                            Duration.ofHours(1),
                            "Same hour, previous week",
                            Duration.ofDays(7)
                    ),
                    getLogSummary.apply(
                            "Last 24 hours",
                            Duration.ofDays(1),
                            "Previous 24 hours",
                            Duration.ofDays(1),
                            "Same 24 hours, previous week",
                            Duration.ofDays(7)
                    )
            );
        };
    }

    private ComponentStateLogSummary getLogSummary(
            AwsProfileAndRegion profileAndRegion,
            List<String> logGroupNames,
            String name,
            Duration duration,
            Duration offset
    ) {
        ZonedDateTime now = ZonedDateTime.now(clock);
        ZonedDateTime endTime = now.minus(offset);
        ZonedDateTime startTime = endTime.minus(duration);
        List<ComponentStateLogLevel> levels = getLevels(
                profileAndRegion,
                logGroupNames,
                startTime.toInstant(),
                endTime.toInstant()
        );
        if (levels.isEmpty()) {
            return null;
        }
        return ComponentStateLogSummary.builder()
                .name(name)
                .levels(levels)
                .startTimestamp(startTime.toLocalDateTime())
                .endTimestamp(endTime.toLocalDateTime())
                .updateTimestamp(LocalDateTime.from(now))
                .build();
    }

    private List<ComponentStateLogLevel> getLevels(
            AwsProfileAndRegion profileAndRegion,
            List<String> logGroupNames,
            Instant startTime,
            Instant endTime
    ) {
        List<ComponentStateLogLevel> levels = mapMessageCountResults(
                executeMessageCountQuery(
                        profileAndRegion,
                        logGroupNames,
                        startTime,
                        endTime
                )
        );
        return levels.stream()
                .map(level -> level.withTopMessages(mapTopMessageResults(
                        executeTopMessageQuery(
                                profileAndRegion,
                                logGroupNames,
                                level,
                                startTime,
                                endTime
                        )
                )))
                .collect(toList());
    }

    private CloudWatchLogsQueryResults executeMessageCountQuery(
            AwsProfileAndRegion profileAndRegion,
            List<String> logGroupNames,
            Instant startTime,
            Instant endTime
    ) {
        return executeQuery(
                profileAndRegion,
                logGroupNames,
                startTime,
                endTime,
                "stats count(*) as message_count by " + logLevelField + " as level\n" +
                        "| sort message_count desc\n" +
                        "| limit 10"
        );
    }

    private CloudWatchLogsQueryResults executeTopMessageQuery(
            AwsProfileAndRegion profileAndRegion,
            List<String> logGroupNames,
            ComponentStateLogLevel level,
            Instant startTime,
            Instant endTime
    ) {
        return executeQuery(
                profileAndRegion,
                logGroupNames,
                startTime,
                endTime,
                "filter " + logLevelField + " = '" + level.getLevel() + "'\n" +
                        "| stats count(*) as message_count by " + logMessageField + " as message\n" +
                        "| sort message_count desc\n" +
                        "| limit 10"
        );
    }

    private List<String> getLogGroupNamesForProfileAndRegionAndComponent(
            AwsProfileAndRegion profileAndRegion,
            Component component
    ) {
        Map<String, List<String>> logGroupNamesByComponent = logGroupNamesByProfileAndRegion.stream()
                .filter(it -> Objects.equals(it.getKey(), profileAndRegion))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(Map.of());
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
        String queryId = clientFacade.startQuery(
                profileAndRegion,
                startTime.getEpochSecond(),
                endTime.getEpochSecond(),
                logGroupNames,
                query
        );

        return retry.executeCallable(() -> {
            CloudWatchLogsQueryResults results = clientFacade.getQueryResults(
                    profileAndRegion,
                    queryId
            );

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

    private List<ComponentStateLogSummary> filterNotNull(ComponentStateLogSummary... values) {
        return Stream.of(values)
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    @FunctionalInterface
    private interface GetLogSummary {

        ComponentStateLogSummary apply(
                String name,
                Duration duration,
                String comparisonName1,
                Duration offset1,
                String comparisonName2,
                Duration offset2
        );
    }
}
