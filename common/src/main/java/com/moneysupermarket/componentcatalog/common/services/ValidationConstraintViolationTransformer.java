package com.moneysupermarket.componentcatalog.common.services;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

@Service
public class ValidationConstraintViolationTransformer {

    public static final Comparator<ConstraintViolation<?>> CONSTRAINT_VIOLATION_COMPARATOR = Comparator
            .<ConstraintViolation<?>, String>comparing(constraintViolation -> constraintViolation.getPropertyPath().toString())
            .thenComparing(ConstraintViolation::getMessage);

    public <T> String transform(Set<ConstraintViolation<T>> constraintViolations) {
        return constraintViolations.stream()
                .sorted(CONSTRAINT_VIOLATION_COMPARATOR)
                .map(constraintViolation ->  String.format("- %s with value \"%s\" %s", constraintViolation.getPropertyPath(),
                        constraintViolation.getInvalidValue(), constraintViolation.getMessage()))
                .collect(Collectors.joining(lineSeparator()));
    }
}
