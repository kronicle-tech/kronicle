package tech.kronicle.service.tests.componentdependencies;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.*;
import tech.kronicle.sdk.models.zipkin.ZipkinState;
import tech.kronicle.sdk.models.zipkin.ZipkinDependency;
import tech.kronicle.service.constants.CommonComponentTypeIds;
import tech.kronicle.service.tests.models.TestContext;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class BackendForFrontendDependencyTestTest {

    public static final String NOT_A_SERVICE = "not-a-service";
    private final BackendForFrontendDependencyTest underTest = new BackendForFrontendDependencyTest();

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheTest() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Checks whether a [backend for frontend (BFF)](https://samnewman.io/patterns/architectural/bff/) component is called "
                + "by services.  ");
    }

    @Test
    public void priorityShouldReturnHigh() {
        // When
        Priority returnValue = underTest.priority();

        // Then
        assertThat(returnValue).isEqualTo(Priority.HIGH);
    }

    @Test
    public void testShouldReturnNotApplicableWhenComponentTypeIsNotBackendForFrontend() {
        // Given
        Component component = Component.builder()
                .type(CommonComponentTypeIds.SERVICE)
                .build();
        TestContext testContext = TestContext.builder().build();

        // When
        TestResult returnValue = underTest.test(component, testContext);

        // Then
        assertThat(returnValue).isEqualTo(TestResult.builder()
                .testId("backend-for-frontend-dependency")
                .outcome(TestOutcome.NOT_APPLICABLE)
                .priority(Priority.HIGH)
                .message("Component is not a [backend for frontend (BFF)](https://samnewman.io/patterns/architectural/bff/)")
                .build());
    }

    @Test
    public void testShouldReturnNotApplicableWhenComponentIsNotIntegratedWithZipkin() {
        // Given
        Component component = createComponent(null);
        TestContext testContext = TestContext.builder().build();

        // When
        TestResult returnValue = underTest.test(component, testContext);

        // Then
        assertThat(returnValue).isEqualTo(TestResult.builder()
                .testId("backend-for-frontend-dependency")
                .outcome(TestOutcome.NOT_APPLICABLE)
                .priority(Priority.HIGH)
                .message("Component does not use Zipkin for distributed tracing")
                .build());
    }

    @Test
    public void testShouldReturnNotApplicableWhenComponentHasNoUpstreamDependenciesInZipkin() {
        // Given
        Component component = createComponent(ZipkinState.builder().build());
        TestContext testContext = TestContext.builder().build();

        // When
        TestResult returnValue = underTest.test(component, testContext);

        // Then
        assertThat(returnValue).isEqualTo(TestResult.builder()
                .testId("backend-for-frontend-dependency")
                .outcome(TestOutcome.NOT_APPLICABLE)
                .priority(Priority.HIGH)
                .message("Component has no upstream dependencies recorded in Zipkin")
                .build());
    }

    @Test
    public void testShouldReturnPassWhenComponentHasAnUpstreamDependencyInZipkinAndItIsANotService() {
        // Given
        Component component = createComponent(
                ZipkinState.builder()
                        .upstream(List.of(
                                ZipkinDependency.builder()
                                        .parent("test-component-id-1")
                                        .build()))
                        .build()
        );
        TestContext testContext = TestContext.builder()
                .componentMap(Map.ofEntries(
                        Map.entry("test-component-id-1", Component.builder().type(NOT_A_SERVICE).build())))
                .build();

        // When
        TestResult returnValue = underTest.test(component, testContext);

        // Then
        assertThat(returnValue).isEqualTo(TestResult.builder()
                .testId("backend-for-frontend-dependency")
                .outcome(TestOutcome.PASS)
                .priority(Priority.HIGH)
                .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasAnUpstreamDependencyInZipkinAndItIsAService() {
        // Given
        Component component = createComponent(
                ZipkinState.builder()
                        .upstream(List.of(
                                ZipkinDependency.builder()
                                        .parent("test-component-id-1")
                                        .build()))
                        .build()
        );
        TestContext testContext = TestContext.builder()
                .componentMap(Map.ofEntries(
                        Map.entry("test-component-id-1", Component.builder().type(CommonComponentTypeIds.SERVICE).build())))
                .build();

        // When
        TestResult returnValue = underTest.test(component, testContext);

        // Then
        assertThat(returnValue).isEqualTo(TestResult.builder()
                .testId("backend-for-frontend-dependency")
                .outcome(TestOutcome.FAIL)
                .priority(Priority.HIGH)
                .message("Component is a [backend for frontend (BFF)](https://samnewman.io/patterns/architectural/bff/) and is called by 1 service.  "
                        + "It is not good for a BFF to be called by services.  ")
                .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasTwoUpstreamDependenciesInZipkinAndTheirComponentTypesAreBothService() {
        // Given
        Component component = createComponent(
                ZipkinState.builder()
                        .upstream(List.of(
                                ZipkinDependency.builder()
                                        .parent("test-component-id-1")
                                        .build(),
                                ZipkinDependency.builder()
                                        .parent("test-component-id-2")
                                        .build()))
                        .build()
        );
        TestContext testContext = TestContext.builder()
                .componentMap(Map.ofEntries(
                        Map.entry("test-component-id-1", Component.builder().type(CommonComponentTypeIds.SERVICE).build()),
                        Map.entry("test-component-id-2", Component.builder().type(CommonComponentTypeIds.SERVICE).build())))
                .build();

        // When
        TestResult returnValue = underTest.test(component, testContext);

        // Then
        assertThat(returnValue).isEqualTo(TestResult.builder()
                .testId("backend-for-frontend-dependency")
                .outcome(TestOutcome.FAIL)
                .priority(Priority.HIGH)
                .message("Component is a [backend for frontend (BFF)](https://samnewman.io/patterns/architectural/bff/) and is called by 2 services.  "
                        + "It is not good for a BFF to be called by services.  ")
                .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasTwoUpstreamDependenciesInZipkinAndOneIsNotAServiceAndOneIsAService() {
        // Given
        Component component = createComponent(
                ZipkinState.builder()
                        .upstream(List.of(
                                ZipkinDependency.builder()
                                        .parent("test-component-id-1")
                                        .build(),
                                ZipkinDependency.builder()
                                        .parent("test-component-id-2")
                                        .build()))
                        .build()
        );
        TestContext testContext = TestContext.builder()
                .componentMap(Map.ofEntries(
                        Map.entry("test-component-id-1", Component.builder().type(NOT_A_SERVICE).build()),
                        Map.entry("test-component-id-2", Component.builder().type(CommonComponentTypeIds.SERVICE).build())))
                .build();

        // When
        TestResult returnValue = underTest.test(component, testContext);

        // Then
        assertThat(returnValue).isEqualTo(TestResult.builder()
                .testId("backend-for-frontend-dependency")
                .outcome(TestOutcome.FAIL)
                .priority(Priority.HIGH)
                .message("Component is a [backend for frontend (BFF)](https://samnewman.io/patterns/architectural/bff/) and is called by 1 service.  "
                        + "It is not good for a BFF to be called by services.  ")
                .build());
    }

    private Component createComponent(ZipkinState zipkinState) {
        return Component.builder()
                .type(CommonComponentTypeIds.BACKEND_FOR_FRONTEND)
                .states(createStates(zipkinState))
                .build();
    }

    private List<@Valid ComponentState> createStates(ZipkinState zipkinState) {
        return nonNull(zipkinState) ? List.of(zipkinState) : List.of();
    }
}