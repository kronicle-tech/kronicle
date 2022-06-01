package tech.kronicle.sdk.models.zipkin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Alias;
import tech.kronicle.sdk.models.Component;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ZipkinStateTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        ZipkinState returnValue = new ObjectMapper().readValue(json, ZipkinState.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeUpstreamAnUnmodifiableList() {
        // Given
        ZipkinState underTest = ZipkinState.builder().upstream(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getUpstream().add(
                ZipkinDependency.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeDownstreamAnUnmodifiableList() {
        // Given
        ZipkinState underTest = ZipkinState.builder().downstream(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getDownstream().add(
                ZipkinDependency.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
