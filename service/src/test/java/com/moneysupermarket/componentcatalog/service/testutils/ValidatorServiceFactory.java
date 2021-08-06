package com.moneysupermarket.componentcatalog.service.testutils;

import com.moneysupermarket.componentcatalog.common.services.ValidationConstraintViolationTransformer;
import com.moneysupermarket.componentcatalog.service.services.ValidationConstraintViolationChecker;
import com.moneysupermarket.componentcatalog.service.services.ValidatorService;

import javax.validation.Validation;

public class ValidatorServiceFactory {

    public static ValidatorService createValidationService() {
        return new ValidatorService(
                Validation.buildDefaultValidatorFactory().getValidator(),
                new ValidationConstraintViolationChecker(new ValidationConstraintViolationTransformer()));
    }
}
