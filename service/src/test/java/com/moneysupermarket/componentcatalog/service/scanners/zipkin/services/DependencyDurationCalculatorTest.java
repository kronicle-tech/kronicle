package com.moneysupermarket.componentcatalog.service.scanners.zipkin.services;

import com.moneysupermarket.componentcatalog.sdk.models.SummaryComponentDependencyDuration;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.models.ObjectWithDurations;
import lombok.Value;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

public class DependencyDurationCalculatorTest {

    private final DependencyDurationCalculator underTest = new DependencyDurationCalculator();

    @Test
    public void calculateDependencyDurationShouldReturnNullWhenListOfDependenciesIsEmpty() {
        // Given
        List<TestDependency> dependencies = List.of();

        // When
        SummaryComponentDependencyDuration returnValue = underTest.calculateDependencyDuration(dependencies);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void calculateDependencyDurationShouldReturnNullWhenAllDependenciesHaveNoDurations() {
        // Given
        List<TestDependency> dependencies = List.of(
                new TestDependency(List.of()),
                new TestDependency(List.of()));

        // When
        SummaryComponentDependencyDuration returnValue = underTest.calculateDependencyDuration(dependencies);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void calculateDependencyDurationShouldHandleAMixReturnOfDependenciesWithAndWithoutDurations() {
        // Given
        List<TestDependency> dependencies = List.of(
                new TestDependency(List.of()),
                new TestDependency(List.of(1L)),
                new TestDependency(List.of()),
                new TestDependency(List.of(2L)));

        // When
        SummaryComponentDependencyDuration returnValue = underTest.calculateDependencyDuration(dependencies);

        // Then
        assertThat(returnValue).isEqualTo(SummaryComponentDependencyDuration.builder()
                .min(1L)
                .max(2L)
                .p50(1L)
                .p90(2L)
                .p99(2L)
                .p99Point9(2L)
                .build());
    }

    @Test
    public void calculateDependencyDurationShouldCalculateTheDurationWhenThereAreExactly1000Inputs() {
        // Given
        List<TestDependency> dependencies = new ArrayList<>();
        LongStream.range(1, 1001).forEach(value -> dependencies.add(new TestDependency(List.of(value))));

        // When
        SummaryComponentDependencyDuration returnValue = underTest.calculateDependencyDuration(dependencies);

        // Then
        assertThat(returnValue).isEqualTo(SummaryComponentDependencyDuration.builder()
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
        List<TestDependency> dependencies = new ArrayList<>();
        LongStream.range(1, 11).forEach(value -> dependencies.add(new TestDependency(List.of(value))));

        // When
        SummaryComponentDependencyDuration returnValue = underTest.calculateDependencyDuration(dependencies);

        // Then
        assertThat(returnValue).isEqualTo(SummaryComponentDependencyDuration.builder()
                .min(1L)
                .max(10L)
                .p50(5L)
                .p90(9L)
                .p99(10L)
                .p99Point9(10L)
                .build());
    }

    @Value
    private static class TestDependency implements ObjectWithDurations {

        List<Long> durations;
    }
}
