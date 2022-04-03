package tech.kronicle.plugins.aws.cloudwatchlogs.services;

import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;
import tech.kronicle.plugins.aws.cloudwatchlogs.client.CloudWatchLogsClientFacade;
import tech.kronicle.plugins.aws.cloudwatchlogs.client.CloudWatchLogsClientFacadeFactory;
import tech.kronicle.plugins.aws.cloudwatchlogs.constants.CloudWatchQueryStatuses;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResult;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResults;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.constants.ResourceTypes;
import tech.kronicle.plugins.aws.guice.CloudWatchLogsGetQueryResultsRetry;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.services.ResourceFetcher;
import tech.kronicle.sdk.models.Alias;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentStateLogLevelCount;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public Map<AwsProfileAndRegion, List<ComponentStateLogLevelCount>> getLogLevelCountsForComponent(Component component) {
        return processProfilesToMap(
                config.getProfiles(),
                getLogLevelCountsProfileAndRegionAndComponent(component)
        );
    }

    private Function<AwsProfileAndRegion, List<ComponentStateLogLevelCount>> getLogLevelCountsProfileAndRegionAndComponent(
            Component component
    ) {
        return profileAndRegion -> {
            Instant now = clock.instant();
            CloudWatchLogsQueryResults finalResults = executeQuery(
                    profileAndRegion,
                    getLogGroupNamesForComponent(profileAndRegion, component),
                    now
            );

            return finalResults.getResults().stream()
                    .map(this::mapResult)
                    .collect(Collectors.toList());
        };
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
            Instant now
    ) {
        if (logGroupNames.isEmpty()) {
            return CloudWatchLogsQueryResults.EMPTY;
        }
        CloudWatchLogsClientFacade clientFacade = clientFacadeFactory.createCloudWatchLogsClientFacade(
                profileAndRegion
        );
        String queryId = clientFacade.startQuery(
                now.minus(Duration.ofHours(1)).getEpochSecond(),
                now.getEpochSecond(),
                logGroupNames,
                "stats count(*) by " + logLevelField + " as level"
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

    private ComponentStateLogLevelCount mapResult(CloudWatchLogsQueryResult result) {
        return ComponentStateLogLevelCount.builder()
                .level(getResultFieldValue(result, "level"))
                .count(Long.parseLong(getResultFieldValue(result, "count(*)")))
                .build();
    }

    private String getResultFieldValue(CloudWatchLogsQueryResult result, String name) {
        return result.getFields().stream()
                .filter(field -> Objects.equals(field.getField(), name))
                .findFirst()
                .get()
                .getValue();
    }
}
