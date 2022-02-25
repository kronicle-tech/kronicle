package tech.kronicle.service.tests.componentdependencies;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.service.constants.CommonComponentTypeIds;
import tech.kronicle.service.tests.ComponentTest;
import tech.kronicle.service.tests.models.TestContext;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;

@org.springframework.stereotype.Component
public class BackendForFrontendDependencyTest extends ComponentTest {

    @Override
    public String description() {
        return "Checks whether a [backend for frontend (BFF)](https://samnewman.io/patterns/architectural/bff/) component is called by services.  ";
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public TestResult test(Component input, TestContext testContext) {
        if (!Objects.equals(input.getTypeId(), CommonComponentTypeIds.BACKEND_FOR_FRONTEND)) {
            return createNotApplicableTestResult("Component is not a [backend for frontend (BFF)](https://samnewman.io/patterns/architectural/bff/)");
        }

        if (isNull(input.getZipkin())) {
            return createNotApplicableTestResult("Component does not use Zipkin for distributed tracing");
        }

        if (isNull(input.getZipkin().getUpstream())) {
            return createNotApplicableTestResult("Component has no upstream dependencies recorded in Zipkin");
        }

        int count = countUpstreamServiceDependencies(input, testContext);

        if (count > 0) {
            return createFailTestResult(
                    String.format("Component is a [backend for frontend (BFF)](https://samnewman.io/patterns/architectural/bff/) and is called by %d %s.  "
                            + "It is not good for a BFF to be called by services.  ",
                            count,
                            (count == 1) ? "service" : "services"));
        } else {
            return createPassTestResult();
        }
    }

    private int countUpstreamServiceDependencies(Component input, TestContext testContext) {
        return (int) input.getZipkin().getUpstream().stream()
                .filter(dependency -> Objects.equals(getComponentTypeIdForComponentId(dependency.getParent(), testContext), CommonComponentTypeIds.SERVICE))
                .count();
    }

    private String getComponentTypeIdForComponentId(String componentId, TestContext testContext) {
        return Optional.ofNullable(testContext.getComponentMap().get(componentId)).map(Component::getTypeId).orElse(null);
    }
}
