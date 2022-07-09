package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class DiagramConnectionTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        DiagramConnection returnValue = new ObjectMapper().readValue(json, DiagramConnection.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void referenceShouldReturnSourceComponentIdAndTargetComponentIdWhenTypeIsNotSet() {
        // Given
        DiagramConnection underTest = DiagramConnection.builder()
                .sourceComponentId("test-source-component-id")
                .targetComponentId("test-target-component-id")
                .build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("test-source-component-id ====> test-target-component-id");
    }

    @Test
    public void referenceShouldReturnSourceComponentIdAndTypeAndTargetComponentIdWhenTypeIsSet() {
        // Given
        DiagramConnection underTest = DiagramConnection.builder()
                .sourceComponentId("test-source-component-id")
                .targetComponentId("test-target-component-id")
                .type("test-type")
                .build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("test-source-component-id ==test-type==> test-target-component-id");
    }

    @Test
    public void constructorShouldMakeTagsAnUnmodifiableList() {
        // Given
        DiagramConnection underTest = DiagramConnection.builder().tags(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTags().add(Tag.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
