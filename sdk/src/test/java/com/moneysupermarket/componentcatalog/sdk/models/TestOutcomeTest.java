package com.moneysupermarket.componentcatalog.sdk.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestOutcomeTest {

    @Test
    public void valueShouldReturnNameInKebabCase() {
        // When
        String returnValue = TestOutcome.NOT_APPLICABLE.value();

        // Then
        assertThat(returnValue).isEqualTo("not-applicable");
    }
}
