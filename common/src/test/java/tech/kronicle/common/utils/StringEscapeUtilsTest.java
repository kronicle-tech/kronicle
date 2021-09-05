package tech.kronicle.common.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringEscapeUtilsTest {

    @Test
    public void escapeStringWhenValueContainsTwoDoubleQuotesShouldReplaceBothDoubleQuotesWithEscapedDoubleQuotes() {
        // Given
        String value = "test1\"test2\"test3";

        // When
        String returnValue = StringEscapeUtils.escapeString(value);

        // Then
        assertThat(returnValue).isEqualTo("test1\\\"test2\\\"test3");
    }

    @Test
    public void escapeStringWhenValueContainsNoDoubleQuotesShouldReturnValueUnaltered() {
        // Given
        String value = "test1test2test3";

        // When
        String returnValue = StringEscapeUtils.escapeString(value);

        // Then
        assertThat(returnValue).isEqualTo("test1test2test3");
    }

    @Test
    public void escapeStringWhenValueIsNullShouldReturnNull() {
        // When
        String returnValue = StringEscapeUtils.escapeString(null);

        // Then
        assertThat(returnValue).isNull();
    }
}
