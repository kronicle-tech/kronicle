package tech.kronicle.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.sonarqube.SummarySonarQubeMetric;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class SummaryTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        Summary returnValue = new ObjectMapper().readValue(json, Summary.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeMissingComponentsAnUnmodifiableList() {
        // Given
        Summary underTest = Summary.builder().missingComponents(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getMissingComponents().add(SummaryMissingComponent.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeSonarQubeMetricsAnUnmodifiableList() {
        // Given
        Summary underTest = Summary.builder().sonarQubeMetrics(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getSonarQubeMetrics().add(SummarySonarQubeMetric.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
