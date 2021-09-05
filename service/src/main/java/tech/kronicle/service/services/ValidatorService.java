package tech.kronicle.service.services;

import tech.kronicle.sdk.models.ObjectWithReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
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
