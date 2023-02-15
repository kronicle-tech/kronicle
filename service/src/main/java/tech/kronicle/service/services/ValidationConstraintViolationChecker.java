package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.common.ValidationConstraintViolationTransformer;
import tech.kronicle.common.StringEscapeUtils;
import tech.kronicle.sdk.models.ObjectWithReference;
import tech.kronicle.service.exceptions.ValidationException;

import jakarta.validation.ConstraintViolation;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ValidationConstraintViolationChecker {

    private final ValidationConstraintViolationTransformer constraintViolationTransformer;

    public <T extends ObjectWithReference> void check(T object, Set<ConstraintViolation<T>> constraintViolations) {
        if (!constraintViolations.isEmpty()) {
            throw new ValidationException(String.format("Failed to validate %s with reference \"%s\". Violations:%n%s", object.getClass().getName(),
                    StringEscapeUtils.escapeString(object.reference()), constraintViolationTransformer.transform(constraintViolations)));
        }
    }
}
