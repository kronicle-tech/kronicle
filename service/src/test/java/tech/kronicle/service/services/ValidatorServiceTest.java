package tech.kronicle.service.services;

import lombok.Value;
import org.junit.jupiter.api.Test;
import tech.kronicle.common.ValidationConstraintViolationTransformer;
import tech.kronicle.sdk.models.ObjectWithReference;
import tech.kronicle.service.exceptions.ValidationException;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ValidatorServiceTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ValidatorService underTest = new ValidatorService(
            validator,
            new ValidationConstraintViolationChecker(new ValidationConstraintViolationTransformer()));

    @Test
    public void validateWhenObjectToValidateIsValidShouldNotThrowAnException() {
        // Given
        TestObject object = new TestObject("test-reference", "test1", "test2");

        // When
        underTest.validate(object);

        // Then: No exception is thrown
    }

    @Test
    public void validateWhenObjectToValidateHas1ValidationIssueShouldThrowAValidationExceptionAndMessageIncludesDetailsOfValidationIssue() {
        // Given
        TestObject object = new TestObject("test-reference", null, null);

        // When
        Throwable thrown = catchThrowable(() -> underTest.validate(object));

        // Then
        assertThat(thrown).isInstanceOf(ValidationException.class);
        assertThat(thrown).hasMessage("Failed to validate tech.kronicle.service.services.ValidatorServiceTest$TestObject with reference \"test-reference\". Violations:\n"
                + "- value1 with value \"null\" must not be empty");
    }

    @Test
    public void validateWhenObjectToValidateHas2ValidationIssuesShouldThrowAValidationExceptionAndMessageIncludesDetailsOfValidationIssues() {
        // Given
        TestObject object = new TestObject("test-reference", null, "does_not_match_pattern");

        // When
        Throwable thrown = catchThrowable(() -> underTest.validate(object));

        // Then
        assertThat(thrown).isInstanceOf(ValidationException.class);
        assertThat(thrown).hasMessage("Failed to validate tech.kronicle.service.services.ValidatorServiceTest$TestObject with reference \"test-reference\". Violations:\n"
                + "- value1 with value \"null\" must not be empty\n"
                + "- value2 with value \"does_not_match_pattern\" must match \"test.*\"");
    }

    @Value
    private static class TestObject implements ObjectWithReference {

        @NotEmpty
        String reference;
        @NotEmpty
        String value1;
        @Pattern(regexp = "test.*")
        String value2;

        @Override
        public String reference() {
            return reference;
        }
    }
}
