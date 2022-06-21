package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class DocTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        Doc returnValue = new ObjectMapper().readValue(json, Doc.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeTagsAnUnmodifiableList() {
        // Given
        Doc underTest = Doc.builder().tags(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTags().add(Tag.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
