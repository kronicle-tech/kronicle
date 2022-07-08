package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ComponentConnectionTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        ComponentConnection returnValue = new ObjectMapper().readValue(json, ComponentConnection.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void referenceShouldReturnTargetComponentIdWhenTypeIsNotSet() {
        // Given
        ComponentConnection underTest = ComponentConnection.builder()
                .targetComponentId("test-target-component-id")
                .build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("====> test-target-component-id");
    }

    @Test
    public void referenceShouldReturnTypeAndTargetComponentIdWhenTypeIsSet() {
        // Given
        ComponentConnection underTest = ComponentConnection.builder()
                .targetComponentId("test-target-component-id")
                .type("test-type")
                .build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("==test-type==> test-target-component-id");
    }

    @Test
    public void constructorShouldMakeTagsAnUnmodifiableList() {
        // Given
        ComponentConnection underTest = ComponentConnection.builder().tags(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTags().add(Tag.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
