package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class GraphEdgeTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        GraphEdge returnValue = new ObjectMapper().readValue(json, GraphEdge.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeRelatedIndexesAnUnmodifiableList() {
        // Given
        GraphEdge underTest = GraphEdge.builder()
                .relatedIndexes(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getRelatedIndexes().add(1));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
