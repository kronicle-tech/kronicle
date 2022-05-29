package tech.kronicle.plugins.aws.cloudwatchlogs.services;

import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;
import tech.kronicle.plugins.aws.AwsPlugin;
import tech.kronicle.plugins.aws.cloudwatchlogs.client.CloudWatchLogsClientFacade;
import tech.kronicle.plugins.aws.cloudwatchlogs.constants.CloudWatchQueryStatuses;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResult;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResultField;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResults;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.constants.ResourceTypes;
import tech.kronicle.plugins.aws.guice.CloudWatchLogsGetQueryResultsRetry;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.models.TaggedResource;
import tech.kronicle.plugins.aws.models.TaggedResourcesByProfileAndRegionAndComponent;
import tech.kronicle.plugins.aws.services.TaggedResourceFinder;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.LogLevelSummary;
import tech.kronicle.sdk.models.LogMessageSummary;
import tech.kronicle.sdk.models.LogSummary;
import tech.kronicle.sdk.models.LogSummaryState;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.plugins.aws.utils.ProfileUtils.processProfilesToMapEntryList;

public class CloudWatchLogsService {

    private final CloudWatchLogsClientFacade clientFacade;
    private final TaggedResourceFinder taggedResourceFinder;
    private final Clock clock;
    private final Retry retry;
    private final AwsConfig config;
    private final String logLevelField;
    private final String logMessageField;
    private TaggedResourcesByProfileAndRegionAndComponent taggedResourcesByProfileAndRegionAndComponent;

    @Inject
    public CloudWatchLogsService(
            CloudWatchLogsClientFacade clientFacade,
            TaggedResourceFinder taggedResourceFinder,
            Clock clock,
            @CloudWatchLogsGetQueryResultsRetry Retry retry,
            AwsConfig config
    ) {
        this.clientFacade = clientFacade;
        this.taggedResourceFinder = taggedResourceFinder;
        this.clock = clock;
        this.retry = retry;
        this.config = config;
        logLevelField = config.getLogFields().getLevel();
        logMessageField = config.getLogFields().getMessage();
    }

    public void refresh() {
        taggedResourcesByProfileAndRegionAndComponent = taggedResourceFinder.getTaggedResourcesByProfileAndRegionAndComponent(
                ResourceTypes.LOGS_LOG_GROUP
        );
    }

    @SneakyThrows
    public List<LogSummaryState> getLogSummariesForComponent(
            Component component
    ) {
        return processProfilesToMapEntryList(
                config.getProfiles(),
                getLogSummariesProfileAndRegionAndComponent(component)
        )
                .stream()
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }

    private Function<AwsProfileAndRegion, List<LogSummaryState>> getLogSummariesProfileAndRegionAndComponent(
            Component component
    ) {
        return profileAndRegion -> {
            Map<String, List<String>> logGroupNamesByEnvironmentId = getLogGroupNamesByEnvironmentId(
                    taggedResourcesByProfileAndRegionAndComponent.getTaggedResources(
                            profileAndRegion,
                            component
                    )
            );
            return logGroupNamesByEnvironmentId.values().stream()
                    .map(logGroupNames -> {
                        GetLogSummary getLogSummary = (
                                boolean enabled,
                                String name,
                                Duration duration,
                                String comparisonName1,
                                Duration offset1,
                                String comparisonName2,
                                Duration offset2
                        ) -> {
                            if (!enabled) {
                                return null;
                            }
                            ZonedDateTime now = ZonedDateTime.now(clock);

                            LogSummary logSummary = getLogSummary(
                                    now,
                                    profileAndRegion,
                                    logGroupNames,
                                    name,
                                    duration,
                                    Duration.ZERO
                            );
                            if (isNull(logSummary)) {
                                return null;
                            }
                            return LogSummaryState.of(
                                    AwsPlugin.ID,
                                    profileAndRegion.getProfile().getEnvironmentId(),
                                    logSummary,
                                    filterNotNull(
                                            getLogSummary(
                                                    now,
                                                    profileAndRegion,
                                                    logGroupNames,
                                                    comparisonName1,
                                                    duration,
                                                    offset1
                                            ),
                                            getLogSummary(
                                                    now,
                                                    profileAndRegion,
                                                    logGroupNames,
                                                    comparisonName2,
                                                    duration,
                                                    offset2
                                            )
                                    ),
                                    now.toLocalDateTime()
                            );
                        };
                        return filterNotNull(
                                getLogSummary.apply(
                                        config.getLogSummaries().getOneHourSummaries(),
                                        "Last hour",
                                        Duration.ofHours(1),
                                        "Previous hour",
                                        Duration.ofHours(1),
                                        "Same hour, previous week",
                                        Duration.ofDays(7)
                                ),
                                getLogSummary.apply(
                                        config.getLogSummaries().getTwentyFourHourSummaries(),
                                        "Last 24 hours",
                                        Duration.ofDays(1),
                                        "Previous 24 hours",
                                        Duration.ofDays(1),
                                        "Same 24 hours, previous week",
                                        Duration.ofDays(7)
                                )
                        );
                    })
                    .flatMap(Collection::stream)
                    .collect(toUnmodifiableList());
        };
    }

    private Map<String, List<String>> getLogGroupNamesByEnvironmentId(List<TaggedResource> taggedResources) {
        return taggedResources.stream()
                .collect(groupingBy(
                        TaggedResource::getEnvironmentId,
                        mapping(TaggedResource::getResourceId, toUnmodifiableList())
                ));
    }

    private LogSummary getLogSummary(
            ZonedDateTime now,
            AwsProfileAndRegion profileAndRegion,
            List<String> logGroupNames,
            String name,
            Duration duration,
            Duration offset
    ) {
        ZonedDateTime endTime = now.minus(offset);
        ZonedDateTime startTime = endTime.minus(duration);
        List<LogLevelSummary> levels = getLevels(
                profileAndRegion,
                logGroupNames,
                startTime.toInstant(),
                endTime.toInstant()
        );
        if (levels.isEmpty()) {
            return null;
        }
        return LogSummary.builder()
                .name(name)
                .levels(levels)
                .startTimestamp(startTime.toLocalDateTime())
                .endTimestamp(endTime.toLocalDateTime())
                .build();
    }

    private List<LogLevelSummary> getLevels(
            AwsProfileAndRegion profileAndRegion,
            List<String> logGroupNames,
            Instant startTime,
            Instant endTime
    ) {
        List<LogLevelSummary> levels = mapMessageCountResults(
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
            LogLevelSummary level,
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

    private List<LogLevelSummary> mapMessageCountResults(
            CloudWatchLogsQueryResults results
    ) {
        return results.getResults().stream()
                .map(this::mapMessageCountResult)
                .collect(toList());
    }

    private LogLevelSummary mapMessageCountResult(CloudWatchLogsQueryResult result) {
        return LogLevelSummary.builder()
                .level(getResultFieldValue(result, "level"))
                .count(Long.parseLong(getResultFieldValue(result, "message_count")))
                .build();
    }

    private List<LogMessageSummary> mapTopMessageResults(CloudWatchLogsQueryResults results) {
        return results.getResults().stream()
                .map(this::mapTopMessageResult)
                .collect(toList());
    }

    private LogMessageSummary mapTopMessageResult(CloudWatchLogsQueryResult result) {
        return LogMessageSummary.builder()
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

    private List<LogSummary> filterNotNull(LogSummary... values) {
        return Stream.of(values)
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private List<LogSummaryState> filterNotNull(LogSummaryState... values) {
        return Stream.of(values)
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    @FunctionalInterface
    private interface GetLogSummary {

        LogSummaryState apply(
                boolean enabled,
                String name,
                Duration duration,
                String comparisonName1,
                Duration offset1,
                String comparisonName2,
                Duration offset2
        );
    }
}
