package tech.kronicle.sdk.models.doc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Tag;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class DocStateTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        DocState returnValue = new ObjectMapper().readValue(json, DocState.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeTagsAnUnmodifiableList() {
        // Given
        DocState underTest = DocState.builder().tags(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTags().add(Tag.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeFilesAnUnmodifiableList() {
        // Given
        DocState underTest = DocState.builder().files(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getFiles().add(DocFile.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
