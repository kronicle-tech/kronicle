package tech.kronicle.pluginutils.services;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class UriVariablesBuilderTest {

    @Test
    public void builderShouldCreateAnEmptyMap() {
        // When
        Map<String, String> returnValue = UriVariablesBuilder.builder()
                .build();

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void addUriVariableShouldAddAUriVariable() {
        // Given
        UriVariablesBuilder underTest = UriVariablesBuilder.builder();

        // When
        Map<String, String> returnValue = underTest.addUriVariable("test-name-1", "test-value-1")
                .build();

        // Then
        assertThat(returnValue).contains(Map.entry("test-name-1", "test-value-1"));
    }

    @Test
    public void addUriVariableShouldAddMultipleUriVariablesWhenCalledMultipleTimes() {
        // Given
        UriVariablesBuilder underTest = UriVariablesBuilder.builder();

        // When
        Map<String, String> returnValue = underTest.addUriVariable("test-name-1", "test-value-1")
                .addUriVariable("test-name-2", "test-value-2")
                .build();

        // Then
        assertThat(returnValue).contains(
                Map.entry("test-name-1", "test-value-1"),
                Map.entry("test-name-2", "test-value-2"));
    }

    @Test
    public void addUriVariableShouldThrowAnExceptionWhenAddingADuplicateUriVariableName() {
        // Given
        UriVariablesBuilder underTest = UriVariablesBuilder.builder()
                .addUriVariable("test-name-1", "test-value-1");

        // When
        Throwable thrown = catchThrowable(() -> underTest.addUriVariable("test-name-1", "test-value-1"));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
        assertThat(thrown).hasMessage("Name test-name-1 already exists");
    }
}
