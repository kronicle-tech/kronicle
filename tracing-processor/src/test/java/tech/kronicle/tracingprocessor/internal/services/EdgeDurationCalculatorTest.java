package tech.kronicle.tracingprocessor.internal.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.GraphEdgeDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

public class EdgeDurationCalculatorTest {

    private final EdgeDurationCalculator underTest = new EdgeDurationCalculator();

    @Test
    public void calculateDependencyDurationShouldReturnNullWhenListOfDurationsIsEmpty() {
        // Given
        List<Long> durations = List.of();

        // When
        GraphEdgeDuration returnValue = underTest.calculateEdgeDuration(durations);

        // Then
        assertThat(returnValue).isNull();
    }
    
    @Test
    public void calculateDependencyDurationShouldCalculateTheDurationWhenThereAreExactly1000Inputs() {
        // Given
        List<Long> durations = new ArrayList<>();
        LongStream.range(1, 1001).forEach(durations::add);

        // When
        GraphEdgeDuration returnValue = underTest.calculateEdgeDuration(durations);

        // Then
        assertThat(returnValue).isEqualTo(GraphEdgeDuration.builder()
                .min(1L)
                .max(1000L)
                .p50(500L)
                .p90(900L)
                .p99(990L)
                .p99Point9(1000L)
                .build());
    }

    @Test
    public void calculateDependencyDurationShouldCalculateTheDurationWhenThereAreASmallNumberOfInputs() {
        // Given
        List<Long> durations = new ArrayList<>();
        LongStream.range(1, 11).forEach(durations::add);

        // When
        GraphEdgeDuration returnValue = underTest.calculateEdgeDuration(durations);

        // Then
        assertThat(returnValue).isEqualTo(GraphEdgeDuration.builder()
                .min(1L)
                .max(10L)
                .p50(5L)
                .p90(9L)
                .p99(10L)
                .p99Point9(10L)
                .build());
    }
}
