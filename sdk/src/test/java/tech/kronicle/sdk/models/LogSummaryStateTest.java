package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class LogSummaryStateTest {

    @Test
    public void constructorShouldMakeLevelsAnUnmodifiableList() {
        // Given
        LogSummaryState underTest = LogSummaryState.builder()
                .levels(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getLevels().add(
                LogLevelState.builder().build())
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
                LogSummaryState.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
