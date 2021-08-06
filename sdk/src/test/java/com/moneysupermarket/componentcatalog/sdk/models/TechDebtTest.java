package com.moneysupermarket.componentcatalog.sdk.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TechDebtTest {

    @Test
    public void constructorShouldSupportDeserializationWithJackson() throws JsonProcessingException {
        // Given
        String json = "{}";

        // When
        TechDebt returnValue = new ObjectMapper().readValue(json, TechDebt.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void constructorShouldMakeLinksAnUnmodifiableList() {
        // Given
        TechDebt underTest = TechDebt.builder().links(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getLinks().add(Link.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
