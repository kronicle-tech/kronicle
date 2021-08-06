package com.moneysupermarket.componentcatalog.service.services;

import com.moneysupermarket.componentcatalog.common.services.ValidationConstraintViolationTransformer;
import com.moneysupermarket.componentcatalog.sdk.models.ObjectWithReference;
import com.moneysupermarket.componentcatalog.service.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static com.moneysupermarket.componentcatalog.common.utils.StringEscapeUtils.escapeString;

@Service
@RequiredArgsConstructor
public class ValidationConstraintViolationChecker {

    private final ValidationConstraintViolationTransformer constraintViolationTransformer;

    public <T extends ObjectWithReference> void check(T object, Set<ConstraintViolation<T>> constraintViolations) {
        if (!constraintViolations.isEmpty()) {
            throw new ValidationException(String.format("Failed to validate %s with reference \"%s\". Violations:%n%s", object.getClass().getName(),
                    escapeString(object.reference()), constraintViolationTransformer.transform(constraintViolations)));
        }
    }
}
