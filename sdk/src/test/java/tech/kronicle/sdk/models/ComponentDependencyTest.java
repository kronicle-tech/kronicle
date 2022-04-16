package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentDependencyTest {

    @Test
    public void referenceShouldReturnTargetComponentIdAndTypeId() {
        // Given
        ComponentDependency underTest = ComponentDependency.builder()
                .targetComponentId("test-target-component-id")
                .typeId("test-type-id")
                .build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("test-target-component-id of type test-type-id");
    }

    @Test
    public void referenceShouldReturnJustTargetComponentIdWhenTypeIdIsNull() {
        // Given
        ComponentDependency underTest = ComponentDependency.builder()
                .targetComponentId("test-target-component-id")
                .build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("test-target-component-id");
    }
}
