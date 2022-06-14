package tech.kronicle.tracingprocessor.internal.models;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.GraphEdge;
import tech.kronicle.sdk.models.GraphNode;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class CollatorGraphTest {

    @Test
    public void constructorShouldMakeNodesAnUnmodifiableList() {
        // Given
        CollatorGraph underTest = CollatorGraph.builder().nodes(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getNodes().add(GraphNode.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeEdgesAnUnmodifiableList() {
        // Given
        CollatorGraph underTest = CollatorGraph.builder().edges(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getEdges().add(CollatorGraphEdge.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
