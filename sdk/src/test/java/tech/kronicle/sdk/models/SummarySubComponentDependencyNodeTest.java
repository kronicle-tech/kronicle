package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class SummarySubComponentDependencyNodeTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        SummarySubComponentDependencyNode returnValue = new ObjectMapper().readValue(json, SummarySubComponentDependencyNode.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeTagsAnUnmodifiableMap() {
        // Given
        SummarySubComponentDependencyNode underTest = SummarySubComponentDependencyNode.builder().tags(new HashMap<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTags().put("test", "test"));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
