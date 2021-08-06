package com.moneysupermarket.componentcatalog.sdk.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PriorityTest {

    @Test
    public void valueShouldReturnNameInKebabCase() {
        // When
        String returnValue = Priority.VERY_HIGH.value();

        // Then
        assertThat(returnValue).isEqualTo("very-high");
    }
}
