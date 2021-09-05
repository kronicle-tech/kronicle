package tech.kronicle.service.tests.sonarqube;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.sdk.models.sonarqube.SonarQubeMeasure;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SonarQubeCodeCoverageTestTest {

    private final SonarQubeCodeCoverageTest underTest = new SonarQubeCodeCoverageTest();

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheTest() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Checks whether a component has at least 80% unit test code coverage");
    }

    @Test
    public void priorityShouldReturnVeryHigh() {
        // When
        Priority returnValue = underTest.priority();

        // Then
        assertThat(returnValue).isEqualTo(Priority.VERY_HIGH);
    }

    @Test
    public void testShouldReturnNotApplicableWhenComponentHasNoSonarQubeProjects() {
        // Given
        Component component = Component.builder().build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-code-coverage")
                        .outcome(TestOutcome.NOT_APPLICABLE)
                        .priority(Priority.VERY_HIGH)
                        .message("Component does not have a SonarQube project")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasASonarQubeProjectWithNoMetrics() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-code-coverage")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.VERY_HIGH)
                        .message("At least one SonarQube project does not include a code coverage metric:\n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) does not have a code coverage metric")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasASonarQubeProjectWithNoCodeCoverageMetric() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .measures(List.of(
                                        SonarQubeMeasure.builder()
                                                .metric("test-metric-key-1")
                                                .value("1")
                                                .build()))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-code-coverage")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.VERY_HIGH)
                        .message("At least one SonarQube project does not include a code coverage metric:\n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) does not have a code coverage metric")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasASonarQubeProjectWithCodeCoverageMetricThatIsLowerThanMinimum() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .measures(List.of(
                                        SonarQubeMeasure.builder()
                                                .metric("coverage")
                                                .value("79")
                                                .build()))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-code-coverage")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.VERY_HIGH)
                        .message("At least one SonarQube project has code coverage below the minimum:\n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) has 79.0% unit test code coverage")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasASonarQubeProjectWithNoCodeCoverageMetricAndASonarQubeProjectWithCodeCoverageMetricThatIsLowerThanMinimum() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .measures(List.of(
                                        SonarQubeMeasure.builder()
                                                .metric("coverage")
                                                .value("79")
                                                .build()))
                                .build(),
                        SonarQubeProject.builder()
                                .key("test-project-key-2")
                                .name("Test Project Name 2")
                                .url("https://example.com/test-project-key-2")
                                .measures(List.of(
                                        SonarQubeMeasure.builder()
                                                .metric("test-metric-key-1")
                                                .value("1")
                                                .build()))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-code-coverage")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.VERY_HIGH)
                        .message("At least one SonarQube project does not include a code coverage metric:\n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) has 79.0% unit test code coverage\n"
                                + "* [test-project-key-2](https://example.com/test-project-key-2) does not have a code coverage metric")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasASonarQubeProjectWithCodeCoverageMetricThatIsLowerThanMinimumAndASonarQubeProjectWithCodeCoverageMetricThatIsHigherThanMinimum() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .measures(List.of(
                                        SonarQubeMeasure.builder()
                                                .metric("coverage")
                                                .value("79")
                                                .build()))
                                .build(),
                        SonarQubeProject.builder()
                                .key("test-project-key-2")
                                .name("Test Project Name 2")
                                .url("https://example.com/test-project-key-2")
                                .measures(List.of(
                                        SonarQubeMeasure.builder()
                                                .metric("coverage")
                                                .value("81")
                                                .build()))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-code-coverage")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.VERY_HIGH)
                        .message("At least one SonarQube project has code coverage below the minimum:\n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) has 79.0% unit test code coverage\n"
                                + "* [test-project-key-2](https://example.com/test-project-key-2) has 81.0% unit test code coverage")
                        .build());
    }

    @Test
    public void testShouldReturnPassWhenComponentHasOneSonarQubeProjectWithCodeCoverageMetricThatIsEqualToMinimum() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .measures(List.of(
                                        SonarQubeMeasure.builder()
                                                .metric("coverage")
                                                .value("80")
                                                .build()))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-code-coverage")
                        .outcome(TestOutcome.PASS)
                        .priority(Priority.VERY_HIGH)
                        .message("All SonarQube project(s) have unit test code coverage that satisfies the minimum:\n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) has 80.0% unit test code coverage")
                        .build());
    }

    @Test
    public void testShouldReturnPassWhenComponentHasOneSonarQubeProjectWithCodeCoverageMetricThatIsHigherThanMinimum() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .measures(List.of(
                                        SonarQubeMeasure.builder()
                                                .metric("coverage")
                                                .value("81")
                                                .build()))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-code-coverage")
                        .outcome(TestOutcome.PASS)
                        .priority(Priority.VERY_HIGH)
                        .message("All SonarQube project(s) have unit test code coverage that satisfies the minimum:\n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) has 81.0% unit test code coverage")
                        .build());
    }

    @Test
    public void testShouldReturnPassWhenComponentHasTwoSonarQubeProjectsWithCodeCoverageMetricThatIsHigherThanMinimum() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .measures(List.of(
                                        SonarQubeMeasure.builder()
                                                .metric("coverage")
                                                .value("81")
                                                .build()))
                                .build(),
                        SonarQubeProject.builder()
                                .key("test-project-key-2")
                                .name("Test Project Name 2")
                                .url("https://example.com/test-project-key-2")
                                .measures(List.of(
                                        SonarQubeMeasure.builder()
                                                .metric("coverage")
                                                .value("81")
                                                .build()))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-code-coverage")
                        .outcome(TestOutcome.PASS)
                        .priority(Priority.VERY_HIGH)
                        .message("All SonarQube project(s) have unit test code coverage that satisfies the minimum:\n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) has 81.0% unit test code coverage\n"
                                + "* [test-project-key-2](https://example.com/test-project-key-2) has 81.0% unit test code coverage")
                        .build());
    }
}