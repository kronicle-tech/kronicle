package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ComponentStateLogSummaryTest {

    @Test
    public void constructorShouldMakeLevelsAnUnmodifiableList() {
        // Given
        ComponentStateLogSummary underTest = ComponentStateLogSummary.builder()
                .levels(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getLevels().add(
                ComponentStateLogLevel.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeComparisonsAnUnmodifiableList() {
        // Given
        ComponentStateLogSummary underTest = ComponentStateLogSummary.builder()
                .comparisons(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getComparisons().add(
                ComponentStateLogSummary.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
