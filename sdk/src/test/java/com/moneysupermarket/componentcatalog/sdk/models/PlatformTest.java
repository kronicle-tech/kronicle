package com.moneysupermarket.componentcatalog.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlatformTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        Platform returnValue = new ObjectMapper().readValue(json, Platform.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void referenceShouldReturnId() {
        // Given
        Platform underTest = Platform.builder().id("test-id").build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("test-id");
    }
}
