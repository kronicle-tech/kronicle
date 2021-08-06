package com.moneysupermarket.componentcatalog.service.tests;

import com.moneysupermarket.componentcatalog.sdk.models.ObjectWithReference;
import com.moneysupermarket.componentcatalog.sdk.models.Priority;
import com.moneysupermarket.componentcatalog.sdk.models.TestOutcome;
import com.moneysupermarket.componentcatalog.sdk.models.TestResult;
import com.moneysupermarket.componentcatalog.service.tests.models.TestContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestTest {

    @Test
    public void idShouldReturnSimpleClassNameByDefaultWithSimpleClassNameConvertedToKebabCaseAndAnyTestSuffixRemoved() {
        // Given
        com.moneysupermarket.componentcatalog.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("example");
    }

    @Test
    public void notesShouldReturnNullByDefault() {
        // Given
        com.moneysupermarket.componentcatalog.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void createNotApplicableTestResultWithNoMessageShouldCreateANotApplicableTestResultWithNoMessage() {
        // Given
        com.moneysupermarket.componentcatalog.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        TestResult returnValue = underTest.createNotApplicableTestResult();

        // Then
        assertThat(returnValue).isEqualTo(new TestResult("example", TestOutcome.NOT_APPLICABLE, Priority.VERY_HIGH, null));
    }

    @Test
    public void createNotApplicableTestResultWithAMessageShouldCreateANotApplicableTestResultWithAMessage() {
        // Given
        com.moneysupermarket.componentcatalog.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        TestResult returnValue = underTest.createNotApplicableTestResult("Test Message");

        // Then
        assertThat(returnValue).isEqualTo(new TestResult("example", TestOutcome.NOT_APPLICABLE, Priority.VERY_HIGH, "Test Message"));
    }

    @Test
    public void createPassTestResultWithNoMessageShouldCreateAPassTestResultWithNoMessage() {
        // Given
        com.moneysupermarket.componentcatalog.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        TestResult returnValue = underTest.createPassTestResult();

        // Then
        assertThat(returnValue).isEqualTo(new TestResult("example", TestOutcome.PASS, Priority.VERY_HIGH, null));
    }

    @Test
    public void createPassTestResultWithAMessageShouldCreateAPassTestResultWithAMessage() {
        // Given
        com.moneysupermarket.componentcatalog.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        TestResult returnValue = underTest.createPassTestResult("Test Message");

        // Then
        assertThat(returnValue).isEqualTo(new TestResult("example", TestOutcome.PASS, Priority.VERY_HIGH, "Test Message"));
    }

    @Test
    public void createFailTestResultWithAMessageShouldCreateAFailTestResultWithAMessage() {
        // Given
        com.moneysupermarket.componentcatalog.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        TestResult returnValue = underTest.createFailTestResult("Test Message");

        // Then
        assertThat(returnValue).isEqualTo(new TestResult("example", TestOutcome.FAIL, Priority.VERY_HIGH, "Test Message"));
    }

    private static class ExampleType implements ObjectWithReference {

        @Override
        public String reference() {
            return null;
        }
    }

    private static class ExampleTest extends com.moneysupermarket.componentcatalog.service.tests.Test<ExampleType> {

        @Override
        public String description() {
            return null;
        }

        @Override
        public Priority priority() {
            return Priority.VERY_HIGH;
        }

        @Override
        public TestResult test(ExampleType input, TestContext testContext) {
            return null;
        }

        @Override
        public TestResult createNotApplicableTestResult() {
            return super.createNotApplicableTestResult();
        }

        @Override
        public TestResult createNotApplicableTestResult(String message) {
            return super.createNotApplicableTestResult(message);
        }

        @Override
        public TestResult createPassTestResult() {
            return super.createPassTestResult();
        }

        @Override
        public TestResult createPassTestResult(String message) {
            return super.createPassTestResult(message);
        }

        @Override
        public TestResult createFailTestResult(String message) {
            return super.createFailTestResult(message);
        }
    }
}
