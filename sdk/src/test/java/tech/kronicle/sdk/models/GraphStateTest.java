package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class GraphStateTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        GraphState returnValue = new ObjectMapper().readValue(json, GraphState.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeNodesAnUnmodifiableList() {
        // Given
        GraphState underTest = GraphState.builder()
                .nodes(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getNodes().add(
                GraphNode.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeEdgesAnUnmodifiableList() {
        // Given
        GraphState underTest = GraphState.builder()
                .edges(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getEdges().add(
                GraphEdge.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
