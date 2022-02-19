package tech.kronicle.service.scanners.gradle.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tech.kronicle.service.scanners.gradle.internal.services.PropertyExpander;
import tech.kronicle.service.scanners.gradle.internal.services.PropertyRetriever;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyExpanderTest {

    private PropertyExpander underTest = new PropertyExpander(new PropertyRetriever());

    @Test
    public void shouldTreatUnderscoreAsPartOfBraceLessPropertyName() {
        // Given
        Map<String, String> properties = new HashMap<>();
        properties.put("one_two", "test_value");

        // When
        String returnValue = underTest.expandProperties("$one_two", "testName", properties, false);

        // Given
        assertThat(returnValue).isEqualTo("test_value");
    }

    @ParameterizedTest
    @ValueSource(strings = {"one_two", "one.two"})
    public void shouldExpandPropertyWhenBracesAreNotRequired(String propertyName) {
        // Given
        Map<String, String> properties = new HashMap<>();
        properties.put(propertyName, "test_value");

        // When
        String returnValue = underTest.expandProperties("$" + propertyName, "testName", properties, false);

        // Given
        assertThat(returnValue).isEqualTo("test_value");
    }

    @ParameterizedTest
    @ValueSource(strings = {"one_two", "one.two"})
    public void shouldExpandPropertyWhenBracesAreRequired(String propertyName) {
        // Given
        Map<String, String> properties = new HashMap<>();
        properties.put(propertyName, "test_value");

        // When
        String returnValue = underTest.expandProperties("${" + propertyName + "}", "testName", properties, true);

        // Given
        assertThat(returnValue).isEqualTo("test_value");
    }

    @Test
    public void shouldIgnoreNullPropertyValue() {
        // Given
        Map<String, String> properties = new HashMap<>();

        // When
        String returnValue = underTest.expandProperties("before${does_not_exist}after", "testName", properties, true);

        // Given
        assertThat(returnValue).isEqualTo("before${does_not_exist}after");
    }

    @Test
    public void shouldSkipDollarSignWithoutBraceWhenBracesAreRequired() {
        // Given
        Map<String, String> properties = new HashMap<>();
        properties.put("one_two", "test_value");

        // When
        String returnValue = underTest.expandProperties("before$${one_two}after", "testName", properties, true);

        // Given
        assertThat(returnValue).isEqualTo("before$test_valueafter");
    }
}
