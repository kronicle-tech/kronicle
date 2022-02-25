package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.service.tests.ComponentTest;
import tech.kronicle.service.tests.models.TestContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestEngineTest {

    @Mock
    private TestFinder mockTestFinder;
    private TestEngine underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new TestEngine(mockTestFinder);
    }

    @Test
    public void testShouldExecuteATestAgainstAComponent() {
        // Given
        when(mockTestFinder.getComponentTests()).thenReturn(List.of(
                new TestComponentTest("1")));
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(
                createTestComponent("1"));

        // When
        underTest.test(componentMap);

        // Then
        assertThat(componentMap).contains(
                Map.entry(
                        "test-component-id-1",
                        Component.builder()
                                .id("test-component-id-1")
                                .testResults(List.of(
                                        new TestResult("test-test-id-1", TestOutcome.PASS, Priority.VERY_HIGH, "test-test-id-1 test-component-id-1")))
                                .build()));
    }

    @Test
    public void testShouldExecuteATestAgainstMultipleComponents() {
        // Given
        when(mockTestFinder.getComponentTests()).thenReturn(List.of(
                new TestComponentTest("1")));
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(
                createTestComponent("1"),
                createTestComponent("2"));

        // When
        underTest.test(componentMap);

        // Then
        assertThat(componentMap).contains(
                Map.entry(
                        "test-component-id-1",
                        Component.builder()
                                .id("test-component-id-1")
                                .testResults(List.of(
                                        new TestResult("test-test-id-1", TestOutcome.PASS, Priority.VERY_HIGH, "test-test-id-1 test-component-id-1")))
                                .build()),
                Map.entry(
                        "test-component-id-2",
                        Component.builder()
                                .id("test-component-id-2")
                                .testResults(List.of(
                                        new TestResult("test-test-id-1", TestOutcome.FAIL, Priority.VERY_HIGH, "test-test-id-1 test-component-id-2")))
                                .build()));
    }

    @Test
    public void testShouldExecuteMultipleTestsAgainstMultipleComponents() {
        // Given
        when(mockTestFinder.getComponentTests()).thenReturn(List.of(
                new TestComponentTest("1"),
                new TestComponentTest("2")));
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(
                createTestComponent("1"),
                createTestComponent("2"));

        // When
        underTest.test(componentMap);

        // Then
        assertThat(componentMap).contains(
                Map.entry(
                        "test-component-id-1",
                        Component.builder()
                                .id("test-component-id-1")
                                .testResults(List.of(
                                        new TestResult("test-test-id-1", TestOutcome.PASS, Priority.VERY_HIGH, "test-test-id-1 test-component-id-1"),
                                        new TestResult("test-test-id-2", TestOutcome.PASS, Priority.VERY_HIGH, "test-test-id-2 test-component-id-1")))
                                .build()),
                Map.entry(
                        "test-component-id-2",
                        Component.builder()
                                .id("test-component-id-2")
                                .testResults(List.of(
                                        new TestResult("test-test-id-1", TestOutcome.FAIL, Priority.VERY_HIGH, "test-test-id-1 test-component-id-2"),
                                        new TestResult("test-test-id-2", TestOutcome.FAIL, Priority.VERY_HIGH, "test-test-id-2 test-component-id-2")))
                                .build()));
    }

    @Test
    public void testShouldCatchAnExceptionRaisedByATest() {
        // Given
        when(mockTestFinder.getComponentTests()).thenReturn(List.of(
                new TestExceptionThrowingComponentTest("1")));
        ConcurrentHashMap<String, Component> componentMap = createComponentMap(
                createTestComponent("1"));

        // When
        underTest.test(componentMap);

        // Then
        assertThat(componentMap).contains(
                Map.entry(
                        "test-component-id-1",
                        Component.builder()
                                .id("test-component-id-1")
                                .testResults(List.of(
                                        new TestResult("test-test-id-1", TestOutcome.FAIL, Priority.VERY_HIGH, "Test generated an error:\n"
                                            + "\n"
                                            + "```\n"
                                            + "test-test-id-1 test-component-id-1\n"
                                            + "```\n")))
                                .build()));
    }

    private Component createTestComponent(String uniquePart) {
        return Component.builder().id("test-component-id-" + uniquePart).build();
    }

    private ConcurrentHashMap<String, Component> createComponentMap(Component... components) {
        ConcurrentHashMap<String, Component> componentMap = new ConcurrentHashMap<>();
        Arrays.stream(components).forEach(component -> componentMap.put(component.getId(), component));
        return componentMap;
    }

    @RequiredArgsConstructor
    private static class TestComponentTest extends ComponentTest {

        private final String uniquePart;

        @Override
        public String id() {
            return "test-test-id-" + uniquePart;
        }

        @Override
        public String description() {
            return "Test Description " + uniquePart;
        }

        @Override
        public Priority priority() {
            return Priority.VERY_HIGH;
        }

        @Override
        public TestResult test(Component input, TestContext testContext) {
            return new TestResult(
                    id(),
                    Objects.equals(input.getId(), "test-component-id-1") ? TestOutcome.PASS : TestOutcome.FAIL,
                    priority(),
                    id() + " " + input.getId());
        }
    }

    private class TestExceptionThrowingComponentTest extends TestComponentTest {

        public TestExceptionThrowingComponentTest(String uniquePart) {
            super(uniquePart);
        }

        @Override
        public TestResult test(Component input, TestContext testContext) {
            throw new RuntimeException(id() + " " + input.getId());
        }
    }
}
