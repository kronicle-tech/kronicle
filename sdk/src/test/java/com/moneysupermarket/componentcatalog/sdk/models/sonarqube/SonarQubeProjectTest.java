package com.moneysupermarket.componentcatalog.sdk.models.sonarqube;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class SonarQubeProjectTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        SonarQubeProject returnValue = new ObjectMapper().readValue(json, SonarQubeProject.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeMeasuresAnUnmodifiableList() {
        // Given
        SonarQubeProject underTest = SonarQubeProject.builder().measures(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getMeasures().add(SonarQubeMeasure.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
