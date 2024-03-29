package tech.kronicle.service.testutils;

import tech.kronicle.common.ValidationConstraintViolationTransformer;
import tech.kronicle.service.services.ValidationConstraintViolationChecker;
import tech.kronicle.service.services.ValidatorService;

import jakarta.validation.Validation;

public class ValidatorServiceFactory {

    public static ValidatorService createValidationService() {
        return new ValidatorService(
                Validation.buildDefaultValidatorFactory().getValidator(),
                new ValidationConstraintViolationChecker(new ValidationConstraintViolationTransformer()));
    }
}
