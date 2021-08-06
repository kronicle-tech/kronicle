package com.moneysupermarket.componentcatalog.service.tests.readme;

import com.moneysupermarket.componentcatalog.sdk.models.Component;
import com.moneysupermarket.componentcatalog.sdk.models.Priority;
import com.moneysupermarket.componentcatalog.sdk.models.readme.Readme;
import com.moneysupermarket.componentcatalog.sdk.models.TestResult;
import com.moneysupermarket.componentcatalog.service.spring.stereotypes.Test;
import com.moneysupermarket.componentcatalog.service.tests.ComponentTest;
import com.moneysupermarket.componentcatalog.service.tests.models.TestContext;

import static java.util.Objects.isNull;

@Test
public class ReadmeTest extends ComponentTest {

    @Override
    public String description() {
        return "Checks whether a component's repo includes a README file at its root";
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public TestResult test(Component input, TestContext testContext) {
        Readme readme = input.getReadme();
        if (isNull(readme)) {
            return createFailTestResult("Component has no README file");
        } else if (readme.getContent().strip().isEmpty()) {
            return createFailTestResult("Component has an empty README file");
        } else {
            return createPassTestResult("Component has a README file");
        }
    }
}
