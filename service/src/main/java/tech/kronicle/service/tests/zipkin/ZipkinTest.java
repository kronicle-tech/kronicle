package tech.kronicle.service.tests.zipkin;

import lombok.RequiredArgsConstructor;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.sdk.models.zipkin.ZipkinState;
import tech.kronicle.service.tests.ComponentTest;
import tech.kronicle.service.tests.models.TestContext;
import tech.kronicle.service.tests.zipkin.config.ZipkinTestConfig;

import java.util.Optional;

import static java.util.Objects.isNull;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class ZipkinTest extends ComponentTest {

    private final ZipkinTestConfig config;

    @Override
    public String description() {
        return "Checks whether various types of components are using [Zipkin](https://zipkin.io/) for\n"
                + "[distributed tracing](https://microservices.io/patterns/observability/distributed-tracing.html)";
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public TestResult test(Component input, TestContext testContext) {
        if (componentTypeIdNotExpectedToUseZipkin(input.getType())) {
            return createNotApplicableTestResult(String.format("Zipkin is not relevant for component type %s", input.getType()));
        }

        if (isZipkinUsed(input)) {
            return createPassTestResult("Component is already using Zipkin for distributed tracing");
        } else {
            return createFailTestResult("We cannot visualise the dependencies and interactions between this component and other components "
                    + "because the component is not using [Zipkin](https://zipkin.io/) for\n"
                    + "[distributed tracing](https://microservices.io/patterns/observability/distributed-tracing.html)");
        }
    }

    private boolean componentTypeIdNotExpectedToUseZipkin(String componentTypeId) {
        return isNull(config.getExpectedComponentTypeIds()) || !config.getExpectedComponentTypeIds().contains(componentTypeId);
    }

    private Boolean isZipkinUsed(Component input) {
        ZipkinState zipkin = input.getState(ZipkinState.TYPE);
        return Optional.ofNullable(zipkin).map(ZipkinState::getUsed).orElse(false);
    }
}
