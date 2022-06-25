package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;
import tech.kronicle.sdk.models.openapi.OpenApiSpecsState;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class OpenApiSpecsStateTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        OpenApiSpecsState returnValue = new ObjectMapper().readValue(json, OpenApiSpecsState.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeOpenApiSpecsAnUnmodifiableList() {
        // Given
        OpenApiSpecsState underTest = OpenApiSpecsState.builder().openApiSpecs(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getOpenApiSpecs().add(
                OpenApiSpec.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
