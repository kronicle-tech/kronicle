package tech.kronicle.utils;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class NullUtilsTest {

    @Test
    public void firstNonNullShouldReturnNullWhenValuesArrayIsEmpty() {
        // Given
        Stream<String> values = Stream.of();

        // When
        String returnValue = NullUtils.firstNonNull(values);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void firstNonNullShouldReturnTheValueWhenValuesArrayContainsOneNonNullValue() {
        // Given
        Stream<String> values = Stream.of("test-1");

        // When
        String returnValue = NullUtils.firstNonNull(values);

        // Then
        assertThat(returnValue).isEqualTo("test-1");
    }

    @Test
    public void firstNonNullShouldReturnTheFirstValueWhenValuesArrayContainsMultipleNonNullValues() {
        // Given
        Stream<String> values = Stream.of("test-1", "test-2", "test-3");

        // When
        String returnValue = NullUtils.firstNonNull(values);

        // Then
        assertThat(returnValue).isEqualTo("test-1");
    }

    @Test
    public void firstNonNullShouldReturnTheSecondValueWhenValuesArrayContainsMultipleValuesAndTheFirstValueIsNull() {
        // Given
        Stream<String> values = Stream.of(null, "test-2", "test-3");

        // When
        String returnValue = NullUtils.firstNonNull(values);

        // Then
        assertThat(returnValue).isEqualTo("test-2");
    }

    @Test
    public void firstNonNullShouldReturnNullWhenValuesArrayContainsMultipleNullValues() {
        // Given
        Stream<String> values = Stream.of(null, null, null);

        // When
        String returnValue = NullUtils.firstNonNull(values);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void firstNonNullShouldThrowAnExceptionWhenValuesArrayItselfIsNull() {
        // When
        Throwable thrown = catchThrowable(() -> NullUtils.firstNonNull(null));

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
        assertThat(thrown).hasMessage("values");
    }
}
