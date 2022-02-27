package tech.kronicle.common;

import lombok.Value;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static tech.kronicle.common.ValidatorServiceFactory.createValidatorService;

public class ValidatorServiceTest {

    private final ValidatorService underTest = createValidatorService();

    @Test
    public void validateShouldDoNothingWhenValueIsValid() {
       // Given
        TestValue value = new TestValue("test-text");

        // When
        underTest.validate(value);

        // Then, nothing happens
    }

    @Test
    public void validateShouldThrowAValidationExceptionWhenValueIsNotValid() {
        // Given
        TestValue value = new TestValue(null);

        // When
        Throwable thrown = catchThrowable(() -> underTest.validate(value));

        // Then, nothing happens
        assertThat(thrown).isInstanceOf(ValidationException.class);
        assertThat(thrown).hasMessage("Validation failed:\n" +
                "- text with value \"null\" must not be null");
    }

    @Test
    public void validateShouldUseTheCustomFailureMessageWhenOneIsProvided() {
        // Given
        TestValue value = new TestValue(null);

        // When
        Throwable thrown = catchThrowable(() -> underTest.validate(value, "Test failure message"));

        // Then, nothing happens
        assertThat(thrown).isInstanceOf(ValidationException.class);
        assertThat(thrown).hasMessage("Test failure message:\n" +
                "- text with value \"null\" must not be null");
    }

    @Value
    public static class TestValue {

        @NotNull
        String text;
    }
}
