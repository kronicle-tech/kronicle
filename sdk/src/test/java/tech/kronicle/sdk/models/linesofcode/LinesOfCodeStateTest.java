package tech.kronicle.sdk.models.linesofcode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class LinesOfCodeStateTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        LinesOfCodeState returnValue = new ObjectMapper().readValue(json, LinesOfCodeState.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeFileExtensionCountsAnUnmodifiableList() {
        // Given
        LinesOfCodeState underTest = LinesOfCodeState.builder().fileExtensionCounts(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getFileExtensionCounts().add(
                FileExtensionCount.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
