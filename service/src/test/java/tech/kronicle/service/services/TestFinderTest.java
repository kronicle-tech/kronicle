package tech.kronicle.service.services;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ObjectWithReference;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.service.tests.ComponentTest;
import tech.kronicle.service.tests.models.TestContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestFinderTest {

    @Test
    public void getComponentTestsShouldReturnTheComponentTests() {
        // Given
        TestComponentTest componentTest1 = new TestComponentTest();
        TestComponentTest componentTest2 = new TestComponentTest();
        TestFinder underTest = new TestFinder(List.of(componentTest1, componentTest2));

        // When
        List<ComponentTest> returnValue = underTest.getComponentTests();

        // Then
        assertThat(returnValue).containsExactly(componentTest1, componentTest2);
    }

    @Test
    public void getComponentTestsShouldIgnoreOtherTypesOfTest() {
        // Given
        TestExampleTypeTest exampleTypeTest1 = new TestExampleTypeTest();
        TestComponentTest componentTest1 = new TestComponentTest();
        TestFinder underTest = new TestFinder(List.of(exampleTypeTest1, componentTest1));

        // When
        List<ComponentTest> returnValue = underTest.getComponentTests();

        // Then
        assertThat(returnValue).containsExactly(componentTest1);
    }

    @Test
    public void getAllTestsShouldReturnAllTheTests() {
        // Given
        TestExampleTypeTest exampleTypeTest1 = new TestExampleTypeTest();
        TestExampleTypeTest exampleTypeTest2 = new TestExampleTypeTest();
        TestComponentTest componentTest1 = new TestComponentTest();
        TestComponentTest componentTest2 = new TestComponentTest();
        List<tech.kronicle.service.tests.Test<?>> tests = List.of(exampleTypeTest1, exampleTypeTest2, componentTest1, componentTest2);
        TestFinder underTest = new TestFinder(tests);

        // When
        List<tech.kronicle.service.tests.Test<?>> returnValue = underTest.getAllTests();

        // Then
        assertThat(returnValue).isSameAs(tests);
    }

    @Test
    public void getTestShouldReturnATestWithMatchingId() {
        // Given
        TestComponentTest componentTest1 = new TestComponentTest();
        TestComponentTest componentTest2 = new TestComponentTest();
        TestFinder underTest = new TestFinder(List.of(componentTest1, componentTest2));

        // When
        tech.kronicle.service.tests.Test<?> returnValue = underTest.getTest(componentTest1.id());

        // Then
        assertThat(returnValue).isSameAs(componentTest1);
    }

    @Test
    public void getTestShouldReturnNullWhenIdDoesNotMatchATest() {
        // Given
        String componentId = "unknown";
        TestExampleTypeTest test1 = new TestExampleTypeTest();
        TestFinder underTest = new TestFinder(List.of(test1));

        // When
        tech.kronicle.service.tests.Test<?> returnValue = underTest.getTest(componentId);

        // Then
        assertThat(returnValue).isNull();
    }

    private static class ExampleType implements ObjectWithReference {

        @Override
        public String reference() {
            return null;
        }
    }

    private static class TestExampleTypeTest extends tech.kronicle.service.tests.Test<ExampleType> {

        @Override
        public String description() {
            return null;
        }

        @Override
        public Priority priority() {
            return null;
        }

        @Override
        public TestResult test(ExampleType input, TestContext testContext) {
            return null;
        }
    }

    private static class TestComponentTest extends ComponentTest {

        @Override
        public String description() {
            return null;
        }

        @Override
        public Priority priority() {
            return null;
        }

        @Override
        public TestResult test(Component input, TestContext testContext) {
            return null;
        }
    }
}
