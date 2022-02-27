package tech.kronicle.common;

import lombok.Value;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;
import tech.kronicle.common.ValidationConstraintViolationTransformer;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationConstraintViolationTransformerTest {

    private ValidationConstraintViolationTransformer underTest = new ValidationConstraintViolationTransformer();

    @Test
    public void transformShouldHandleAnEmptyConstraintValidationSet() {
        // Given
        Set<ConstraintViolation<TestObject>> constraintViolationSet = Set.of();

        // When
        String returnValue = underTest.transform(constraintViolationSet);

        // Then
        assertThat(returnValue).isEqualTo("");
    }

    @Test
    public void transformShouldTransformASingleConstraintValidation() {
        // Given
        Set<ConstraintViolation<TestObject>> constraintViolationSet = Set.of(
                createTestConstraintViolation("has failed in some way", new TestInvalidValue("Test Invalid Value"), "test.property.path"));

        // When
        String returnValue = underTest.transform(constraintViolationSet);

        // Then
        assertThat(returnValue).isEqualTo("- test.property.path with value \"Test Invalid Value\" has failed in some way");
    }

    @Test
    public void transformShouldSortConstraintValidationsByPropertyPathThenMessage() {
        // Given
        Set<ConstraintViolation<TestObject>> constraintViolationSet = Set.of(
                createTestConstraintViolation("Test Message 1", new TestInvalidValue("Test Invalid Value 2"), "test.property.path2"),
                createTestConstraintViolation("Test Message 2", new TestInvalidValue("Test Invalid Value 1"), "test.property.path2"),
                createTestConstraintViolation("Test Message 3", new TestInvalidValue("Test Invalid Value 4"), "test.property.path1"),
                createTestConstraintViolation("Test Message 4", new TestInvalidValue("Test Invalid Value 3"), "test.property.path1"));

        // When
        String returnValue = underTest.transform(constraintViolationSet);

        // Then
        assertThat(returnValue).isEqualTo(""
                + "- test.property.path1 with value \"Test Invalid Value 4\" Test Message 3\n"
                + "- test.property.path1 with value \"Test Invalid Value 3\" Test Message 4\n"
                + "- test.property.path2 with value \"Test Invalid Value 2\" Test Message 1\n"
                + "- test.property.path2 with value \"Test Invalid Value 1\" Test Message 2");
    }

    private ConstraintViolation<TestObject> createTestConstraintViolation(String message, TestInvalidValue invalidValue, String propertyPath) {
        return ConstraintViolationImpl.forBeanValidation(null, null, null, message, null,
                null, null, invalidValue, PathImpl.createPathFromString(propertyPath), null,
                null);
    }

    private static class TestObject {
    }

    @Value
    private static class TestInvalidValue {
        String stringValue;

        @Override
        public String toString() {
            return stringValue;
        }
    }
}
