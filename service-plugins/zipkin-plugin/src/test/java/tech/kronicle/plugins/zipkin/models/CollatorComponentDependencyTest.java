package tech.kronicle.plugins.zipkin.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class CollatorComponentDependencyTest {

    @Test
    public void getTimestampsShouldReturnASingletonListContainingTheTimestampWhenTheTimestampIsNotNull() {
        // Given
        CollatorComponentDependency underTest = CollatorComponentDependency.builder()
                .timestamp(1L)
                .build();

        // When
        List<Long> returnValue = underTest.getTimestamps();

        // Then
        assertThat(returnValue).containsExactly(1L);
    }

    @Test
    public void getTimestampsShouldReturnUnmodifiableListWhenTheTimestampIsNotNull() {
        // Given
        CollatorComponentDependency underTest = CollatorComponentDependency.builder()
                .timestamp(1L)
                .build();
        
        // When
        Throwable thrown = catchThrowable(() -> underTest.getTimestamps().add(1L));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void getTimestampsShouldReturnAnEmptyListWhenTheTimestampIsNull() {
        // Given
        CollatorComponentDependency underTest = CollatorComponentDependency.builder()
                .timestamp(null)
                .build();

        // When
        List<Long> returnValue = underTest.getTimestamps();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getTimestampsShouldReturnUnmodifiableListWhenTheTimestampIsNull() {
        // Given
        CollatorComponentDependency underTest = CollatorComponentDependency.builder()
                .timestamp(null)
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTimestamps().add(1L));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void getDurationsShouldReturnASingletonListContainingTheDurationWhenTheDurationIsNotNull() {
        // Given
        CollatorComponentDependency underTest = CollatorComponentDependency.builder()
                .duration(1L)
                .build();

        // When
        List<Long> returnValue = underTest.getDurations();

        // Then
        assertThat(returnValue).containsExactly(1L);
    }

    @Test
    public void getDurationsShouldReturnUnmodifiableListWhenTheDurationIsNotNull() {
        // Given
        CollatorComponentDependency underTest = CollatorComponentDependency.builder()
                .duration(1L)
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getDurations().add(1L));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void getDurationsShouldReturnAnEmptyListWhenTheDurationIsNull() {
        // Given
        CollatorComponentDependency underTest = CollatorComponentDependency.builder()
                .duration(null)
                .build();

        // When
        List<Long> returnValue = underTest.getDurations();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getDurationsShouldReturnUnmodifiableListWhenTheDurationIsNull() {
        // Given
        CollatorComponentDependency underTest = CollatorComponentDependency.builder()
                .duration(null)
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getDurations().add(1L));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
