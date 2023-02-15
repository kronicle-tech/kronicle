package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.sdk.models.ObjectWithReference;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ValidatorService {

    private final Validator validator;
    private final ValidationConstraintViolationChecker constraintViolationChecker;

    public <T extends ObjectWithReference> void validate(T object) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);
        constraintViolationChecker.check(object, constraintViolations);
    }
}
