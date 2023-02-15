package tech.kronicle.common;

import lombok.RequiredArgsConstructor;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

@RequiredArgsConstructor
public class ValidatorService {

    private final Validator validator;
    private final ValidationConstraintViolationTransformer constraintViolationTransformer;

    public <T> void validate(T value) {
        validate(value, "Validation failed");
    }

    public <T> void validate(T value, String failureMessage) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(value);

        if (!constraintViolations.isEmpty()) {
            throw new ValidationException(
                    String.format(failureMessage + ":%n%s", constraintViolationTransformer.transform(constraintViolations))
            );
        }
    }
}
