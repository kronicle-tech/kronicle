package tech.kronicle.service.tests.sonarqube;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.*;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.service.tests.sonarqube.config.SonarQubeTestConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SonarQubeTestTest extends BaseSonarQubeTestTest {

    public static final String COMPONENT_TEST_ID_EXPECTED_TO_USE_SONARQUBE = "component-test-id-expected-to-use-sonarqube";
    public static final String COMPONENT_TEST_ID_NOT_EXPECTED_TO_USE_SONARQUBE = "component-test-id-not-expected-to-use-sonarqube";
    private final SonarQubeTest underTest = new SonarQubeTest(new SonarQubeTestConfig(List.of(COMPONENT_TEST_ID_EXPECTED_TO_USE_SONARQUBE)));

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheTest() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Checks whether a component has an associated SonarQube project");
    }

    @Test
    public void priorityShouldReturnVeryHigh() {
        // When
        Priority returnValue = underTest.priority();

        // Then
        assertThat(returnValue).isEqualTo(Priority.HIGH);
    }

    @Test
    public void testShouldReturnPassWhenComponentHasASonarQubeProject() {
        // Given
        Component component = createComponent(List.of(
                SonarQubeProject.builder()
                        .key("test-project-key-1")
                        .name("Test Project Name 1")
                        .url("https://example.com/test-project-key-1")
                        .build()
        ));

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube")
                        .outcome(TestOutcome.PASS)
                        .priority(Priority.HIGH)
                        .message("Component has these SonarQube project(s):\n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1)")
                        .build());
    }

    @Test
    public void testShouldReturnNotApplicableWhenComponentHasNoSonarQubeProjectsAndComponentTypeIsNotExpectedToHaveSonarQubeProjects() {
        // Given
        Component component = Component.builder()
                .typeId(COMPONENT_TEST_ID_NOT_EXPECTED_TO_USE_SONARQUBE)
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube")
                        .outcome(TestOutcome.NOT_APPLICABLE)
                        .priority(Priority.HIGH)
                        .message("Component type component-test-id-not-expected-to-use-sonarqube is not expected to have a SonarQube project")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasNoSonarQubeProjectsAndComponentTypeIsExpectedToHaveSonarQubeProjects() {
        // Given
        Component component = Component.builder()
                .typeId(COMPONENT_TEST_ID_EXPECTED_TO_USE_SONARQUBE)
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.HIGH)
                        .message("Component does not have a SonarQube project.  The SonarQube Scanner auto detects which SonarQube projects a component has by search its codebase for any files that contain a line of text that contains both the word `sonar` and the key of any SonarQube project defined on the SonarQube server.  For example, the scanner would detect a SonarQube project key from this: \n"
                                + "\n"
                                + "```\n"
                                + "property 'sonar.projectKey', 'com.example:some-project'\n"
                                + "```")
                        .build());
    }
}