package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class SoftwaresStateTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        SoftwaresState returnValue = new ObjectMapper().readValue(json, SoftwaresState.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeSoftwaresAnUnmodifiableList() {
        // Given
        SoftwaresState underTest = SoftwaresState.builder().softwares(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getSoftwares().add(
                Software.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
