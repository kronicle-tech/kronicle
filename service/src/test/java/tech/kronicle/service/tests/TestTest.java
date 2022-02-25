package tech.kronicle.service.tests;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.ObjectWithReference;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.service.tests.models.TestContext;

import static org.assertj.core.api.Assertions.assertThat;

public class TestTest {

    @Test
    public void idShouldReturnSimpleClassNameByDefaultWithSimpleClassNameConvertedToKebabCaseAndAnyTestSuffixRemoved() {
        // Given
        tech.kronicle.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("example");
    }

    @Test
    public void notesShouldReturnNullByDefault() {
        // Given
        tech.kronicle.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void createNotApplicableTestResultWithNoMessageShouldCreateANotApplicableTestResultWithNoMessage() {
        // Given
        tech.kronicle.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        TestResult returnValue = underTest.createNotApplicableTestResult();

        // Then
        assertThat(returnValue).isEqualTo(new TestResult("example", TestOutcome.NOT_APPLICABLE, Priority.VERY_HIGH, null));
    }

    @Test
    public void createNotApplicableTestResultWithAMessageShouldCreateANotApplicableTestResultWithAMessage() {
        // Given
        tech.kronicle.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        TestResult returnValue = underTest.createNotApplicableTestResult("Test Message");

        // Then
        assertThat(returnValue).isEqualTo(new TestResult("example", TestOutcome.NOT_APPLICABLE, Priority.VERY_HIGH, "Test Message"));
    }

    @Test
    public void createPassTestResultWithNoMessageShouldCreateAPassTestResultWithNoMessage() {
        // Given
        tech.kronicle.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        TestResult returnValue = underTest.createPassTestResult();

        // Then
        assertThat(returnValue).isEqualTo(new TestResult("example", TestOutcome.PASS, Priority.VERY_HIGH, null));
    }

    @Test
    public void createPassTestResultWithAMessageShouldCreateAPassTestResultWithAMessage() {
        // Given
        tech.kronicle.service.tests.Test<ExampleType> underTest = new ExampleTest();

        // When
        TestResult returnValue = underTest.createPassTestResult("Test Message");

        // Then
        assertThat(returnValue).isEqualTo(new TestResult("example", TestOutcome.PASS, Priority.VERY_HIGH, "Test Message"));
    }

    @Test
    public void createFailTestResultWithAMessageShouldCreateAFailTestResultWithAMessage() {
        // Given
        tech.kronicle.service.tests.Test<ExampleType> underTest = new ExampleTest();

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

    private static class ExampleTest extends tech.kronicle.service.tests.Test<ExampleType> {

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
