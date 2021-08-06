package com.moneysupermarket.componentcatalog.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScannerTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        Scanner returnValue = new ObjectMapper().readValue(json, Scanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void referenceShouldReturnId() {
        // Given
        Scanner underTest = Scanner.builder().id("test-scanner-id").build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("test-scanner-id");
    }
}
