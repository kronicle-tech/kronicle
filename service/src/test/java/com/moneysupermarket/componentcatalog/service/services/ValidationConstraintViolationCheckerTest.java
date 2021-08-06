package com.moneysupermarket.componentcatalog.service.services;

import com.moneysupermarket.componentcatalog.common.services.ValidationConstraintViolationTransformer;
import com.moneysupermarket.componentcatalog.sdk.models.ObjectWithReference;
import com.moneysupermarket.componentcatalog.service.exceptions.ValidationException;
import lombok.Value;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidationConstraintViolationCheckerTest {

    private ValidationConstraintViolationChecker underTest;
    @Mock
    private ValidationConstraintViolationTransformer mockTransformer;

    @BeforeEach
    public void beforeEach() {
        underTest = new ValidationConstraintViolationChecker(mockTransformer);
    }

    @Test
    public void checkShouldDoNothingWhenConstraintViolationSetIsEmpty() {
        // Given
        TestObject object = new TestObject("Test Reference");
        Set<ConstraintViolation<TestObject>> constraintViolationSet = Set.of();

        // When
        underTest.check(object, constraintViolationSet);

        // Then
        // Nothing happens
    }

    @Test
    public void checkShouldThrowAValidationExceptionWhenConstraintViolationSetIsNotEmpty() {
        // Given
        TestObject object = new TestObject("Test Reference");
        Set<ConstraintViolation<TestObject>> constraintViolationSet = Set.of(createTestConstraintViolation());
        when(mockTransformer.transform(constraintViolationSet)).thenReturn("Mocked Transformed Constraint Violation Set");

        // When
        Throwable thrown = catchThrowable(() -> underTest.check(object, constraintViolationSet));

        // Then
        assertThat(thrown).isInstanceOf(ValidationException.class);
        assertThat(thrown).hasMessage("Failed to validate "
                + "com.moneysupermarket.componentcatalog.service.services.ValidationConstraintViolationCheckerTest$TestObject with reference "
                + "\"Test Reference\". Violations:\n"
                + "Mocked Transformed Constraint Violation Set");
    }

    private ConstraintViolation<TestObject> createTestConstraintViolation() {
        return ConstraintViolationImpl.forBeanValidation(null, null, null, null, null,
                null, null, null, null, null,
                null);
    }

    @Value
    private static class TestObject implements ObjectWithReference {

        String reference;

        @Override
        public String reference() {
            return reference;
        }
    }
}
