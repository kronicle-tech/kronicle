package tech.kronicle.common;

import javax.validation.Validation;

public final class ValidatorServiceFactory {

    public static ValidatorService createValidatorService() {
        return new ValidatorService(
                Validation.buildDefaultValidatorFactory().getValidator(),
                new ValidationConstraintViolationTransformer()
        );
    }

    private ValidatorServiceFactory() {
    }
}
