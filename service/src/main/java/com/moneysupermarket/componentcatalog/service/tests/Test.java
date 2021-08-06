package com.moneysupermarket.componentcatalog.service.tests;

import com.moneysupermarket.componentcatalog.sdk.models.ObjectWithReference;
import com.moneysupermarket.componentcatalog.sdk.models.Priority;
import com.moneysupermarket.componentcatalog.sdk.models.TestOutcome;
import com.moneysupermarket.componentcatalog.sdk.models.TestResult;
import com.moneysupermarket.componentcatalog.service.tests.models.TestContext;

import static com.moneysupermarket.componentcatalog.common.utils.CaseUtils.toKebabCase;

public abstract class Test<I extends ObjectWithReference> {

    public String id() {
        return toKebabCase(getClass().getSimpleName()).replaceFirst("-test$", "");
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
