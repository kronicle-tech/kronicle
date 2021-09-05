package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class CrossFunctionalRequirementTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        CrossFunctionalRequirement returnValue = new ObjectMapper().readValue(json, CrossFunctionalRequirement.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeLinksAnUnmodifiableList() {
        // Given
        CrossFunctionalRequirement underTest = CrossFunctionalRequirement.builder().links(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getLinks().add(Link.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
