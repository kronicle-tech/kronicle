package tech.kronicle.service.tests.readme;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.sdk.models.readme.ReadmeState;
import tech.kronicle.service.tests.ComponentTest;
import tech.kronicle.service.tests.models.TestContext;

import static java.util.Objects.isNull;

@org.springframework.stereotype.Component
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
        ReadmeState readme = input.getState(ReadmeState.TYPE);
        if (isNull(readme)) {
            return createFailTestResult("Component has no README file");
        } else if (readme.getContent().isBlank()) {
            return createFailTestResult("Component has an empty README file");
        } else {
            return createPassTestResult("Component has a README file");
        }
    }
}
