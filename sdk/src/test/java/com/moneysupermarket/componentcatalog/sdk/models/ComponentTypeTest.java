package com.moneysupermarket.componentcatalog.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentTypeTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        ComponentType returnValue = new ObjectMapper().readValue(json, ComponentType.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void referenceShouldReturnId() {
        // Given
        ComponentType underTest = ComponentType.builder().id("test-id").build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("test-id");
    }
}
