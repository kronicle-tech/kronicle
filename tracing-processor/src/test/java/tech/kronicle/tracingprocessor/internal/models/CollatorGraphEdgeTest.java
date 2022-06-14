package tech.kronicle.tracingprocessor.internal.models;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Alias;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdge;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class CollatorGraphEdgeTest {

    @Test
    public void constructorShouldMakeRelatedIndexesAnUnmodifiableList() {
        // Given
        CollatorGraphEdge underTest = CollatorGraphEdge.builder().relatedIndexes(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getRelatedIndexes().add(1));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeTimestampsAnUnmodifiableList() {
        // Given
        CollatorGraphEdge underTest = CollatorGraphEdge.builder().timestamps(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTimestamps().add(1L));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeDurationsAnUnmodifiableList() {
        // Given
        CollatorGraphEdge underTest = CollatorGraphEdge.builder().durations(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getDurations().add(1L));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
