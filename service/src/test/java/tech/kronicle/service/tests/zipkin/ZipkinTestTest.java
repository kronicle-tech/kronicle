package tech.kronicle.service.tests.zipkin;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.sdk.models.zipkin.Zipkin;
import tech.kronicle.service.constants.CommonComponentTypeIds;
import tech.kronicle.service.scanners.zipkin.config.ZipkinConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ZipkinTestTest {

    private final ZipkinTest underTest = new ZipkinTest(new ZipkinConfig(null, null, null, null, null, null, List.of(CommonComponentTypeIds.SERVICE)));

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheTest() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Checks whether various types of components are using [Zipkin](https://zipkin.io/) for\n"
                + "[distributed tracing](https://microservices.io/patterns/observability/distributed-tracing.html)");
    }

    @Test
    public void priorityShouldReturnHigh() {
        // When
        Priority returnValue = underTest.priority();

        // Then
        assertThat(returnValue).isEqualTo(Priority.HIGH);
    }

    @Test
    public void testShouldReturnNotApplicableWhenComponentTypeIsNotRelevantForZipkin() {
        // Given
        Component component = Component.builder()
                .typeId("not-relevant-component-type")
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("zipkin")
                        .outcome(TestOutcome.NOT_APPLICABLE)
                        .priority(Priority.HIGH)
                        .message("Zipkin is not relevant for component type not-relevant-component-type")
                        .build());
    }

    @Test
    public void testShouldReturnPassWhenComponentTypeIsRelevantForZipkinAndZipkinIsAlreadyUsed() {
        // Given
        Component component = Component.builder()
                .typeId(CommonComponentTypeIds.SERVICE)
                .zipkin(Zipkin.builder().used(true).build())
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("zipkin")
                        .outcome(TestOutcome.PASS)
                        .priority(Priority.HIGH)
                        .message("Component is already using Zipkin for distributed tracing")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentTypeIsRelevantForZipkinAndZipkinIsNotUsed() {
        // Given
        Component component = Component.builder()
                .typeId(CommonComponentTypeIds.SERVICE)
                .zipkin(Zipkin.builder().used(false).build())
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("zipkin")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.HIGH)
                        .message("We cannot visualise the dependencies and interactions between this component and other components "
                                + "because the component is not using [Zipkin](https://zipkin.io/) for\n"
                                + "[distributed tracing](https://microservices.io/patterns/observability/distributed-tracing.html)")
                        .build());
    }
}