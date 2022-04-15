package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DependencyTest {

    @Test
    public void referenceShouldReturnSourceComponentIdAndTargetComponentIdAndTypeId() {
        // Given
        Dependency underTest = Dependency.builder()
                .sourceComponentId("test-source-component-id")
                .targetComponentId("test-target-component-id")
                .typeId("test-type-id")
                .build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("test-source-component-id to test-target-component-id of type test-type-id");
    }

    @Test
    public void referenceShouldReturnJustSourceComponentIdAndTargetComponentIdWhenTypeIdIsNull() {
        // Given
        Dependency underTest = Dependency.builder()
                .sourceComponentId("test-source-component-id")
                .targetComponentId("test-target-component-id")
                .build();

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("test-source-component-id to test-target-component-id");
    }
}
