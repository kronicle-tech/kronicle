package com.moneysupermarket.componentcatalog.sdk.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentDependencyTest {

    @Test
    public void referenceShouldReturnId() {
        // Given
        ComponentDependency underTest = ComponentDependency.builder().targetComponentId("test-component-id").build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("test-component-id");
    }
}
