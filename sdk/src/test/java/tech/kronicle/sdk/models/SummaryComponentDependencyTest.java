package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class SummaryComponentDependencyTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        SummaryComponentDependency returnValue = new ObjectMapper().readValue(json, SummaryComponentDependency.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeRelatedIndexesAnUnmodifiableList() {
        // Given
        SummaryComponentDependency underTest = SummaryComponentDependency.builder()
                .relatedIndexes(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getRelatedIndexes().add(1));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
