package tech.kronicle.service.tests;

import tech.kronicle.common.utils.CaseUtils;
import tech.kronicle.sdk.models.ObjectWithReference;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.service.tests.models.TestContext;

public abstract class Test<I extends ObjectWithReference> {

    public String id() {
        return CaseUtils.toKebabCase(getClass().getSimpleName()).replaceFirst("-test$", "");
    }

    public abstract String description();

    public String notes() {
        return null;
    }

    public abstract Priority priority();

    public abstract TestResult test(I input, TestContext testContext);

    private TestResult createTestResult(TestOutcome outcome, String message) {
        return new TestResult(id(), outcome, priority(), message);
    }

    protected TestResult createNotApplicableTestResult() {
        return createNotApplicableTestResult(null);
    }

    protected TestResult createNotApplicableTestResult(String message) {
        return createTestResult(TestOutcome.NOT_APPLICABLE, message);
    }

    protected TestResult createPassTestResult() {
        return createPassTestResult(null);
    }

    protected TestResult createPassTestResult(String message) {
        return createTestResult(TestOutcome.PASS, message);
    }

    protected TestResult createFailTestResult(String message) {
        return createTestResult(TestOutcome.FAIL, message);
    }
}
