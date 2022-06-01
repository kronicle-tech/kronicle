package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.graphql.GraphQlSchema;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class GraphQlSpecsStateTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        GraphQlSpecsState returnValue = new ObjectMapper().readValue(json, GraphQlSpecsState.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeGraphQlSpecsAnUnmodifiableList() {
        // Given
        GraphQlSpecsState underTest = GraphQlSpecsState.builder().graphQlSchemas(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getGraphQlSchemas().add(
                GraphQlSchema.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
