package tech.kronicle.plugins.aws.cloudwatchlogs.services;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.aws.cloudwatchlogs.client.CloudWatchLogsClientFacade;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResult;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResultField;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResults;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.config.AwsLogFieldsConfig;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.config.AwsTagKeysConfig;
import tech.kronicle.plugins.aws.constants.ResourceTypes;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.models.ResourceIdsByProfileAndRegionAndComponent;
import tech.kronicle.plugins.aws.services.TaggedResourceFinder;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.LogLevelState;
import tech.kronicle.sdk.models.LogMessageState;
import tech.kronicle.sdk.models.LogSummaryState;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.aws.testutils.AwsProfileAndRegionUtils.createProfile;
import static tech.kronicle.plugins.aws.testutils.ComponentUtils.createComponent;

@ExtendWith(MockitoExtension.class)
public class CloudWatchLogsServiceTest {

    @Mock
    private CloudWatchLogsClientFacade clientFacade;
    @Mock
    private TaggedResourceFinder taggedResourceFinder;
    private final Instant fixedInstant = LocalDateTime.of(2001, 2, 3, 4, 5, 6).toInstant(ZoneOffset.UTC);
    private final Clock clock = Clock.fixed(
            fixedInstant,
            ZoneOffset.UTC
    );
    private final Retry retry = new FakeRetry();

    @Test
    public void getLogSummariesForComponentShouldGetLogSummariesForAComponent() {
        // Given
        AwsProfileConfig profile1 = createProfile(1);
        AwsProfileConfig profile2 = createProfile(2);
        CloudWatchLogsService underTest = new CloudWatchLogsService(
                clientFacade,
                taggedResourceFinder,
                clock,
                retry,
                new AwsConfig(
                        List.of(profile1, profile2),
                        null,
                        null,
                        null, new AwsTagKeysConfig("component", null),
                        new AwsLogFieldsConfig("test-level-field", "test-message-field")
                )
        );
        AwsProfileAndRegion profile1AndRegion1 = new AwsProfileAndRegion(profile1, profile1.getRegions().get(0));
        AwsProfileAndRegion profile1AndRegion2 = new AwsProfileAndRegion(profile1, profile1.getRegions().get(1));
        AwsProfileAndRegion profile2AndRegion1 = new AwsProfileAndRegion(profile2, profile2.getRegions().get(0));
        AwsProfileAndRegion profile2AndRegion2 = new AwsProfileAndRegion(profile2, profile2.getRegions().get(1));
        Component component = createComponent(1);
        mockTaggedResourceFinder(profile1AndRegion1, component);
        List<String> logGroupNames = List.of(
                createLogGroupName(1),
                createLogGroupName(2)
        );
        AtomicInteger queryNumber = new AtomicInteger(1);
        mockLevelMessageCountQuery(
                profile1AndRegion1,
                logGroupNames,
                queryNumber,
                fixedInstant.minus(Duration.ofHours(1)),
                fixedInstant
        );
        mockLevelMessageCountQuery(
                profile1AndRegion1,
                logGroupNames,
                queryNumber,
                fixedInstant.minus(Duration.ofHours(2)),
                fixedInstant.minus(Duration.ofHours(1))
        );
        mockLevelMessageCountQuery(
                profile1AndRegion1,
                logGroupNames,
                queryNumber,
                fixedInstant.minus(Duration.ofDays(7)).minus(Duration.ofHours(1)),
                fixedInstant.minus(Duration.ofDays(7))
        );
        mockLevelMessageCountQuery(
                profile1AndRegion1,
                logGroupNames,
                queryNumber,
                fixedInstant.minus(Duration.ofDays(1)),
                fixedInstant
        );
        mockLevelMessageCountQuery(
                profile1AndRegion1,
                logGroupNames,
                queryNumber,
                fixedInstant.minus(Duration.ofDays(2)),
                fixedInstant.minus(Duration.ofDays(1))
        );
        mockLevelMessageCountQuery(
                profile1AndRegion1,
                logGroupNames,
                queryNumber,
                fixedInstant.minus(Duration.ofDays(7)).minus(Duration.ofDays(1)),
                fixedInstant.minus(Duration.ofDays(7))
        );

        // When
        underTest.refresh();
        List<Map.Entry<AwsProfileAndRegion, List<LogSummaryState>>> returnValue =
                underTest.getLogSummariesForComponent(component);

        // Then
        assertThat(returnValue).containsExactly(
                Map.entry(
                        profile1AndRegion1,
                        List.of(
                                LogSummaryState.builder()
                                        .name("Last hour")
                                        .startTimestamp(LocalDateTime.of(2001, 2, 3, 3, 5, 6))
                                        .endTimestamp(LocalDateTime.of(2001, 2, 3, 4, 5, 6))
                                        .levels(List.of(
                                                LogLevelState.builder()
                                                        .level("test-level-1-1")
                                                        .count(11L)
                                                        .topMessages(List.of(
                                                                LogMessageState.builder()
                                                                        .message("test-message-1-1-1")
                                                                        .count(111L)
                                                                        .build(),
                                                                LogMessageState.builder()
                                                                        .message("test-message-1-1-2")
                                                                        .count(112L).build(),
                                                                LogMessageState.builder()
                                                                        .message("test-message-1-1-3")
                                                                        .count(113L).build()
                                                        ))
                                                        .build(),
                                                LogLevelState.builder()
                                                        .level("test-level-1-2")
                                                        .count(12L)
                                                        .topMessages(List.of(
                                                                LogMessageState.builder()
                                                                        .message("test-message-1-2-1")
                                                                        .count(121L).build(),
                                                                LogMessageState.builder()
                                                                        .message("test-message-1-2-2")
                                                                        .count(122L).build(),
                                                                LogMessageState.builder()
                                                                        .message("test-message-1-2-3")
                                                                        .count(123L).build()
                                                        ))
                                                        .build(),
                                                LogLevelState.builder()
                                                        .level("test-level-1-3")
                                                        .count(13L)
                                                        .topMessages(List.of(
                                                                LogMessageState.builder()
                                                                        .message("test-message-1-3-1")
                                                                        .count(131L).build(),
                                                                LogMessageState.builder()
                                                                        .message("test-message-1-3-2")
                                                                        .count(132L).build(),
                                                                LogMessageState.builder()
                                                                        .message("test-message-1-3-3")
                                                                        .count(133L).build()
                                                        ))
                                                        .build()
                                        ))
                                        .updateTimestamp(LocalDateTime.of(2001, 2, 3, 4, 5, 6))
                                        .comparisons(List.of(
                                                LogSummaryState.builder()
                                                        .name("Previous hour")
                                                        .startTimestamp(LocalDateTime.of(2001, 2, 3, 2, 5, 6))
                                                        .endTimestamp(LocalDateTime.of(2001, 2, 3, 3, 5, 6))
                                                        .levels(List.of(
                                                                LogLevelState.builder()
                                                                        .level("test-level-2-1")
                                                                        .count(21L)
                                                                        .topMessages(List.of(
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-2-1-1")
                                                                                        .count(211L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-2-1-2")
                                                                                        .count(212L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-2-1-3")
                                                                                        .count(213L).build()
                                                                        ))
                                                                        .build(),
                                                                LogLevelState.builder()
                                                                        .level("test-level-2-2")
                                                                        .count(22L)
                                                                        .topMessages(List.of(
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-2-2-1")
                                                                                        .count(221L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-2-2-2")
                                                                                        .count(222L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-2-2-3")
                                                                                        .count(223L).build()
                                                                        ))
                                                                        .build(),
                                                                LogLevelState.builder()
                                                                        .level("test-level-2-3")
                                                                        .count(23L)
                                                                        .topMessages(List.of(
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-2-3-1")
                                                                                        .count(231L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-2-3-2")
                                                                                        .count(232L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-2-3-3")
                                                                                        .count(233L).build()
                                                                        ))
                                                                        .build()
                                                        ))
                                                        .updateTimestamp(LocalDateTime.of(2001, 2, 3, 4, 5, 6))
                                                        .build(),
                                                LogSummaryState.builder()
                                                        .name("Same hour, previous week")
                                                        .startTimestamp(LocalDateTime.of(2001, 1, 27, 3, 5, 6))
                                                        .endTimestamp(LocalDateTime.of(2001, 1, 27, 4, 5, 6))
                                                        .levels(List.of(
                                                                LogLevelState.builder()
                                                                        .level("test-level-3-1")
                                                                        .count(31L)
                                                                        .topMessages(List.of(
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-3-1-1")
                                                                                        .count(311L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-3-1-2")
                                                                                        .count(312L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-3-1-3")
                                                                                        .count(313L).build()
                                                                        ))
                                                                        .build(),
                                                                LogLevelState.builder()
                                                                        .level("test-level-3-2")
                                                                        .count(32L)
                                                                        .topMessages(List.of(
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-3-2-1")
                                                                                        .count(321L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-3-2-2")
                                                                                        .count(322L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-3-2-3")
                                                                                        .count(323L).build()
                                                                        ))
                                                                        .build(),
                                                                LogLevelState.builder()
                                                                        .level("test-level-3-3")
                                                                        .count(33L)
                                                                        .topMessages(List.of(
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-3-3-1")
                                                                                        .count(331L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-3-3-2")
                                                                                        .count(332L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-3-3-3")
                                                                                        .count(333L).build()
                                                                        ))
                                                                        .build()
                                                        ))
                                                        .updateTimestamp(LocalDateTime.of(2001, 2, 3, 4, 5, 6))
                                                        .build()
                                        ))
                                        .build(),
                                LogSummaryState.builder()
                                        .name("Last 24 hours")
                                        .startTimestamp(LocalDateTime.of(2001, 2, 2, 4, 5, 6))
                                        .endTimestamp(LocalDateTime.of(2001, 2, 3, 4, 5, 6))
                                        .levels(List.of(
                                                LogLevelState.builder()
                                                        .level("test-level-4-1")
                                                        .count(41L)
                                                        .topMessages(List.of(
                                                                LogMessageState.builder()
                                                                        .message("test-message-4-1-1")
                                                                        .count(411L).build(),
                                                                LogMessageState.builder()
                                                                        .message("test-message-4-1-2")
                                                                        .count(412L).build(),
                                                                LogMessageState.builder()
                                                                        .message("test-message-4-1-3")
                                                                        .count(413L).build()
                                                        ))
                                                        .build(),
                                                LogLevelState.builder()
                                                        .level("test-level-4-2")
                                                        .count(42L)
                                                        .topMessages(List.of(
                                                                LogMessageState.builder()
                                                                        .message("test-message-4-2-1")
                                                                        .count(421L).build(),
                                                                LogMessageState.builder()
                                                                        .message("test-message-4-2-2")
                                                                        .count(422L).build(),
                                                                LogMessageState.builder()
                                                                        .message("test-message-4-2-3")
                                                                        .count(423L).build()
                                                        ))
                                                        .build(),
                                                LogLevelState.builder()
                                                        .level("test-level-4-3")
                                                        .count(43L)
                                                        .topMessages(List.of(
                                                                LogMessageState.builder()
                                                                        .message("test-message-4-3-1")
                                                                        .count(431L).build(),
                                                                LogMessageState.builder()
                                                                        .message("test-message-4-3-2")
                                                                        .count(432L).build(),
                                                                LogMessageState.builder()
                                                                        .message("test-message-4-3-3")
                                                                        .count(433L).build()
                                                        ))
                                                        .build()
                                        ))
                                        .updateTimestamp(LocalDateTime.of(2001, 2, 3, 4, 5, 6))
                                        .comparisons(List.of(
                                                LogSummaryState.builder()
                                                        .name("Previous 24 hours")
                                                        .startTimestamp(LocalDateTime.of(2001, 2, 1, 4, 5, 6))
                                                        .endTimestamp(LocalDateTime.of(2001, 2, 2, 4, 5, 6))
                                                        .levels(List.of(
                                                                LogLevelState.builder()
                                                                        .level("test-level-5-1")
                                                                        .count(51L)
                                                                        .topMessages(List.of(
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-5-1-1")
                                                                                        .count(511L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-5-1-2")
                                                                                        .count(512L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-5-1-3")
                                                                                        .count(513L).build()
                                                                        ))
                                                                        .build(),
                                                                LogLevelState.builder()
                                                                        .level("test-level-5-2")
                                                                        .count(52L)
                                                                        .topMessages(List.of(
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-5-2-1")
                                                                                        .count(521L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-5-2-2")
                                                                                        .count(522L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-5-2-3")
                                                                                        .count(523L).build()
                                                                        ))
                                                                        .build(),
                                                                LogLevelState.builder()
                                                                        .level("test-level-5-3")
                                                                        .count(53L)
                                                                        .topMessages(List.of(
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-5-3-1")
                                                                                        .count(531L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-5-3-2")
                                                                                        .count(532L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-5-3-3")
                                                                                        .count(533L).build()
                                                                        ))
                                                                        .build()
                                                        ))
                                                        .updateTimestamp(LocalDateTime.of(2001, 2, 3, 4, 5, 6))
                                                        .build(),
                                                LogSummaryState.builder()
                                                        .name("Same 24 hours, previous week")
                                                        .startTimestamp(LocalDateTime.of(2001, 1, 26, 4, 5, 6))
                                                        .endTimestamp(LocalDateTime.of(2001, 1, 27, 4, 5, 6))
                                                        .levels(List.of(
                                                                LogLevelState.builder()
                                                                        .level("test-level-6-1")
                                                                        .count(61L)
                                                                        .topMessages(List.of(
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-6-1-1")
                                                                                        .count(611L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-6-1-2")
                                                                                        .count(612L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-6-1-3")
                                                                                        .count(613L).build()
                                                                        ))
                                                                        .build(),
                                                                LogLevelState.builder()
                                                                        .level("test-level-6-2")
                                                                        .count(62L)
                                                                        .topMessages(List.of(
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-6-2-1")
                                                                                        .count(621L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-6-2-2")
                                                                                        .count(622L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-6-2-3")
                                                                                        .count(623L).build()
                                                                        ))
                                                                        .build(),
                                                                LogLevelState.builder()
                                                                        .level("test-level-6-3")
                                                                        .count(63L)
                                                                        .topMessages(List.of(
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-6-3-1")
                                                                                        .count(631L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-6-3-2")
                                                                                        .count(632L).build(),
                                                                                LogMessageState.builder()
                                                                                        .message("test-message-6-3-3")
                                                                                        .count(633L).build()
                                                                        ))
                                                                        .build()
                                                        ))
                                                        .updateTimestamp(LocalDateTime.of(2001, 2, 3, 4, 5, 6))
                                                        .build()
                                        ))
                                        .build()
                        )
                ),
                Map.entry(
                        profile1AndRegion2,
                        List.of()
                ),
                Map.entry(
                        profile2AndRegion1,
                        List.of()
                ),
                Map.entry(
                        profile2AndRegion2,
                        List.of()
                )
        );
    }

    private void mockTaggedResourceFinder(AwsProfileAndRegion profile1AndRegion1, Component component) {
        when(taggedResourceFinder.getResourceIdsByProfileAndRegionAndComponent(ResourceTypes.LOGS_LOG_GROUP)).thenReturn(
                createResourceIdsByProfileAndRegionAndComponent(
                        Map.entry(
                                profile1AndRegion1,
                                Map.of(
                                        component.getId(),
                                        List.of(
                                                createLogGroupName(1),
                                                createLogGroupName(2)
                                        )
                                )
                        )
                )
        );
    }

    private void mockLevelMessageCountQuery(
            AwsProfileAndRegion profileAndRegion,
            List<String> logGroupNames,
            AtomicInteger queryNumberState,
            Instant startTime,
            Instant endTime
    ) {
        int queryNumber = queryNumberState.getAndIncrement();
        mockQuery(
                profileAndRegion,
                logGroupNames,
                queryNumber,
                "",
                startTime,
                endTime,
                "stats count(*) as message_count by test-level-field as level\n" +
                "| sort message_count desc\n" +
                "| limit 10",
                List.of(
                        createMessageCountQueryResult(queryNumber, 1),
                        createMessageCountQueryResult(queryNumber, 2),
                        createMessageCountQueryResult(queryNumber, 3)
                )
        );
        mockLevelTopMessageQuery(profileAndRegion, logGroupNames, queryNumber, startTime, endTime, 1);
        mockLevelTopMessageQuery(profileAndRegion, logGroupNames, queryNumber, startTime, endTime, 2);
        mockLevelTopMessageQuery(profileAndRegion, logGroupNames, queryNumber, startTime, endTime, 3);
    }

    private void mockLevelTopMessageQuery(
            AwsProfileAndRegion profileAndRegion,
            List<String> logGroupNames,
            int queryNumber,
            Instant startTime,
            Instant endTime,
            int levelNumber) {
        String level = createLevelField(queryNumber, levelNumber);
        mockQuery(
                profileAndRegion,
                logGroupNames,
                queryNumber,
                "-" + level,
                startTime,
                endTime,
                "filter test-level-field = '" + level + "'\n" +
                        "| stats count(*) as message_count by test-message-field as message\n" +
                        "| sort message_count desc\n" +
                        "| limit 10",
                List.of(
                        createTopMessageCountQueryResult(queryNumber, levelNumber, 1),
                        createTopMessageCountQueryResult(queryNumber, levelNumber, 2),
                        createTopMessageCountQueryResult(queryNumber, levelNumber, 3)
                )
        );
    }

    private void mockQuery(
            AwsProfileAndRegion profileAndRegion,
            List<String> logGroupNames,
            int queryNumber,
            String queryIdSuffix,
            Instant startTime,
            Instant endTime,
            String query,
            List<CloudWatchLogsQueryResult> queryResults
    ) {
        when(clientFacade.startQuery(
                profileAndRegion,
                startTime.getEpochSecond(),
                endTime.getEpochSecond(),
                logGroupNames,
                query
        )).thenReturn("test-query-id-" + queryNumber + queryIdSuffix);
        when(clientFacade.getQueryResults(
                profileAndRegion,
                "test-query-id-" + queryNumber + queryIdSuffix
        )).thenReturn(new CloudWatchLogsQueryResults(
                "Complete",
                queryResults
        ));
    }

    private CloudWatchLogsQueryResult createMessageCountQueryResult(int queryNumber, int levelNumber) {
        return new CloudWatchLogsQueryResult(List.of(
                new CloudWatchLogsQueryResultField("level", createLevelField(queryNumber, levelNumber)),
                new CloudWatchLogsQueryResultField("message_count", createLevelMessageCount(queryNumber, levelNumber))
        ));
    }

    private CloudWatchLogsQueryResult createTopMessageCountQueryResult(int queryNumber, int levelNumber, int messageNumber) {
        return new CloudWatchLogsQueryResult(List.of(
                new CloudWatchLogsQueryResultField("message", createMessageField(queryNumber, levelNumber, messageNumber)),
                new CloudWatchLogsQueryResultField("message_count", createLevelTopMessageCount(queryNumber, levelNumber, messageNumber))
        ));
    }

    private String createLevelField(int queryNumber, int levelNumber) {
        return "test-level-" + queryNumber + "-" + levelNumber;
    }

    private String createMessageField(int queryNumber, int levelNumber, int messageNumber) {
        return "test-message-" + queryNumber + "-" + levelNumber + "-" + messageNumber;
    }

    private String createLevelMessageCount(int queryNumber, int levelNumber) {
        return Integer.toString((queryNumber * 10) + levelNumber);
    }

    private String createLevelTopMessageCount(int queryNumber, int levelNumber, int messageNumber) {
        return Integer.toString((queryNumber * 100) + (levelNumber * 10) + messageNumber);
    }

    private String createLogGroupName(int logGroupNameNumber) {
        return "test-log-group-name-" + logGroupNameNumber;
    }


    private ResourceIdsByProfileAndRegionAndComponent createResourceIdsByProfileAndRegionAndComponent(
            Map.Entry<AwsProfileAndRegion, Map<String, List<String>>> resourceIdsForProfileAndRegionAndComponent
    ) {
        return new ResourceIdsByProfileAndRegionAndComponent(
                List.of(resourceIdsForProfileAndRegionAndComponent)
        );
    }

    private static class FakeRetry implements Retry {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public <T> Context<T> context() {
            return null;
        }

        @Override
        public <T> AsyncContext<T> asyncContext() {
            return null;
        }

        @Override
        public RetryConfig getRetryConfig() {
            return null;
        }

        @Override
        public io.vavr.collection.Map<String, String> getTags() {
            return null;
        }

        @Override
        public EventPublisher getEventPublisher() {
            return null;
        }

        @Override
        public Metrics getMetrics() {
            return null;
        }

        @Override
        public <T> T executeCallable(Callable<T> callable) throws Exception {
            return callable.call();
        }
    }
}
