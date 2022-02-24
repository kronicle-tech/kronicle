package tech.kronicle.pluginutils.utils;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginutils.utils.MarkdownHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class MarkdownHelperTest {

    @Test
    public void createMarkdownLinkShouldCreateAMarkdownLink() {
        // Given
        String text = "Test Text";
        String url = "https://example.com/test-url";

        // When
        String returnValue = MarkdownHelper.createMarkdownLink(text, url);

        // Then
        assertThat(returnValue).isEqualTo("[Test Text](https://example.com/test-url)");
    }
}