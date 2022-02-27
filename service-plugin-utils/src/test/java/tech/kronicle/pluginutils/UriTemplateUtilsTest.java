package tech.kronicle.pluginutils;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginutils.UriTemplateUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UriTemplateUtilsTest {

    @Test
    public void expandUriTemplateShouldReturnOriginalUriTemplateAsTheUriWhenThereAreNoUriVariables() {
        // Given
        Map<String, String> uriVariables = Map.of();

        // When
        String returnValue = UriTemplateUtils.expandUriTemplate("https://example.com/", uriVariables);

        // Then
        assertThat(returnValue).isEqualTo("https://example.com/");
    }

    @Test
    public void expandUriTemplateShouldReplaceASingleUriVariable() {
        // Given
        Map<String, String> uriVariables = Map.ofEntries(
                Map.entry("pathSegment", "test-value")
        );

        // When
        String returnValue = UriTemplateUtils.expandUriTemplate("https://example.com/{pathSegment}", uriVariables);

        // Then
        assertThat(returnValue).isEqualTo("https://example.com/test-value");
    }

    @Test
    public void expandUriTemplateShouldReplaceMultipleUriVariables() {
        // Given
        Map<String, String> uriVariables = Map.ofEntries(
                Map.entry("pathSegment1", "test-value-1"),
                Map.entry("pathSegment2", "test-value-2")
        );

        // When
        String returnValue = UriTemplateUtils.expandUriTemplate("https://example.com/{pathSegment1}/{pathSegment2}", uriVariables);

        // Then
        assertThat(returnValue).isEqualTo("https://example.com/test-value-1/test-value-2");
    }

    @Test
    public void expandUriTemplateShouldIgnoreAnUnusedUriVariable() {
        // Given
        Map<String, String> uriVariables = Map.ofEntries(
                Map.entry("pathSegment", "test-value")
        );

        // When
        String returnValue = UriTemplateUtils.expandUriTemplate("https://example.com/", uriVariables);

        // Then
        assertThat(returnValue).isEqualTo("https://example.com/");
    }

    @Test
    public void expandUriTemplateShouldIgnoreAUriVariablePlaceholderWithNoValue() {
        // Given
        Map<String, String> uriVariables = Map.of();

        // When
        String returnValue = UriTemplateUtils.expandUriTemplate("https://example.com/{pathSegment}", uriVariables);

        // Then
        assertThat(returnValue).isEqualTo("https://example.com/{pathSegment}");
    }
}
