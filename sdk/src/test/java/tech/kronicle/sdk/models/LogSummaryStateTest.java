package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

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
        LogSummaryState returnValue = LogSummaryState.of(
                "test-plugin-id",
                "test-environment-id",
                logSummary,
                createLocalDateTime(1),
                createLocalDateTime(2),
                comparisons,
                createLocalDateTime(3)
        );

        // Then
        assertThat(returnValue).isEqualTo(
                LogSummaryState.builder()
                        .build()
        );
    }
}
