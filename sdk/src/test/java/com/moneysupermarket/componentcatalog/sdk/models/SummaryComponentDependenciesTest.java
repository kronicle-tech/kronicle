package com.moneysupermarket.componentcatalog.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class SummaryComponentDependenciesTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        SummaryComponentDependencies returnValue = new ObjectMapper().readValue(json, SummaryComponentDependencies.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeNodesAnUnmodifiableList() {
        // Given
        SummaryComponentDependencies underTest = new SummaryComponentDependencies(new ArrayList<>(), null);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getNodes().add(SummaryComponentDependencyNode.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeDependenciesAnUnmodifiableList() {
        // Given
        SummaryComponentDependencies underTest = new SummaryComponentDependencies(new ArrayList<>(), null);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getDependencies().add(SummaryComponentDependency.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
