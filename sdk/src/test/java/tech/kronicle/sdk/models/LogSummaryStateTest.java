package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.kronicle.sdk.models.testutils.LocalDateTimeUtils.createLocalDateTime;
import static tech.kronicle.sdk.models.testutils.LogSummaryUtils.createLogSummary;

public class LogSummaryStateTest {

    @Test
    public void constructorShouldMakeLevelsAnUnmodifiableList() {
        // Given
        LogSummaryState underTest = LogSummaryState.builder()
                .levels(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getLevels().add(
                LogLevelSummary.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeComparisonsAnUnmodifiableList() {
        // Given
        LogSummaryState underTest = LogSummaryState.builder()
                .comparisons(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getComparisons().add(
                LogSummary.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void ofShouldMapALogSummaryToALogSummaryState() {
        // Given
        LogSummary logSummary = createLogSummary(1);
        List<LogSummary> comparisons = List.of(
                createLogSummary(2),
                createLogSummary(3)
        );

        // When
        LocalDateTime updateTimestamp = createLocalDateTime(4);
        LogSummaryState returnValue = LogSummaryState.of(
                "test-plugin-id",
                "test-environment-id",
                logSummary,
                comparisons,
                updateTimestamp
        );

        // Then
        assertThat(returnValue).isEqualTo(
                LogSummaryState.builder()
                        .environmentId("test-environment-id")
                        .pluginId("test-plugin-id")
                        .name(createLogSummaryName(1))
                        .startTimestamp(createLocalDateTime(1, 1))
                        .endTimestamp(createLocalDateTime(1, 2))
                        .levels(createLogLevels(1))
                        .comparisons(List.of(
                                createLogSummary(2),
                                createLogSummary(3)
                        ))
                        .updateTimestamp(updateTimestamp)
                        .build()
        );
    }

    private LogSummary createLogSummary(int logSummaryNumber) {
        return LogSummary.builder()
                .name(createLogSummaryName(logSummaryNumber))
                .startTimestamp(createLocalDateTime(logSummaryNumber, 1))
                .endTimestamp(createLocalDateTime(logSummaryNumber, 2))
                .levels(createLogLevels(logSummaryNumber))
                .build();
    }

    private String createLogSummaryName(int logSummaryNumber) {
        return "test-log-summary-" + logSummaryNumber;
    }

    private List<@NotNull @Valid LogLevelSummary> createLogLevels(int logSummaryNumber) {
        return List.of(
                LogLevelSummary.builder()
                        .level("test-log-level-" + logSummaryNumber + "-1")
                        .build(),
                LogLevelSummary.builder()
                        .level("test-log-level-" + logSummaryNumber + "-2")
                        .build()
        );
    }
}
