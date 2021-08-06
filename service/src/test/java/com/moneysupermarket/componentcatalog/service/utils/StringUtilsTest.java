package com.moneysupermarket.componentcatalog.service.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class StringUtilsTest {

    public static final String TEST_NAME = "test_name";

    @Test
    public void requireNonEmptyWhenValueIsNullShouldThrowNullPointerException() {
        // When
        Throwable thrown = catchThrowable(() -> StringUtils.requireNonEmpty(null, TEST_NAME));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage(TEST_NAME);
    }

    @Test
    public void requireNonEmptyWhenValueIsEmptyShouldThrowNullPointerException() {
        // Given
        String value = "";

        // When
        Throwable thrown = catchThrowable(() -> StringUtils.requireNonEmpty(value, TEST_NAME));

        // Then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertThat(thrown).hasMessage(TEST_NAME + " must not be empty");
    }

    @Test
    public void requireNonEmptyWhenValueIsNotEmptyShouldReturnValue() {
        // Given
        String value = "test_value";

        // When
        String returnValue = StringUtils.requireNonEmpty(value, TEST_NAME);

        // Then
        assertThat(returnValue).isEqualTo("test_value");
    }
}
