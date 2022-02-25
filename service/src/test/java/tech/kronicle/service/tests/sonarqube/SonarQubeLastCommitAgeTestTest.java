package tech.kronicle.service.tests.sonarqube;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.sdk.models.sonarqube.SonarQubeMeasure;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SonarQubeLastCommitAgeTestTest {

    private static final Clock clock = Clock.fixed(Instant.parse("2021-01-01T01:02:03.004Z"), ZoneOffset.UTC);
    private final SonarQubeLastCommitAgeTest underTest = new SonarQubeLastCommitAgeTest(clock);

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheTest() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Checks whether a SonarQube project has received an update in the last 6 months");
    }

    @Test
    public void priorityShouldReturnHigh() {
        // When
        Priority returnValue = underTest.priority();

        // Then
        assertThat(returnValue).isEqualTo(Priority.HIGH);
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
                        .testId("sonar-qube-last-commit-age")
                        .outcome(TestOutcome.NOT_APPLICABLE)
                        .priority(Priority.HIGH)
                        .message("Component does not have a SonarQube project")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasASonarQubeProjectWithNoLastCommitAge() {
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
                        .testId("sonar-qube-last-commit-age")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.HIGH)
                        .message("At least one of the component's SonarQube projects has no last commit date metric:\n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) does not have a last commit date")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasASonarQubeProjectWithLastCommitAgeThatIsHigherThanMaximum() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .lastCommitTimestamp(LocalDateTime.of(2020, 6, 1, 1, 2, 3, 4))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-last-commit-age")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.HIGH)
                        .message("At least one of the component's SonarQube project(s) have not been updated with the results of at least 1 commit in the last 7 months. Is the component's build job still configured to send metrics to the SonarQube server? If not, the project(s) metrics in Kronicle will be out-of-date. \n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) was last updated with a commit 7 months ago")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasASonarQubeProjectWithNoLastCommitAgeAndASonarQubeProjectWithLastCommitAgeThatIsHigherThanMaximum() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .build(),
                        SonarQubeProject.builder()
                                .key("test-project-key-2")
                                .name("Test Project Name 2")
                                .url("https://example.com/test-project-key-2")
                                .lastCommitTimestamp(LocalDateTime.of(2020, 6, 1, 1, 2, 3, 4))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-last-commit-age")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.HIGH)
                        .message("At least one of the component's SonarQube projects has no last commit date metric:\n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) does not have a last commit date\n"
                                + "* [test-project-key-2](https://example.com/test-project-key-2) was last updated with a commit 7 months ago")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenComponentHasASonarQubeProjectWithLastCommitAgeThatIsLowerThanMaximumAndASonarQubeProjectWithLastCommitAgeThatIsHigherThanMaximum() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .lastCommitTimestamp(LocalDateTime.of(2020, 8, 1, 1, 2, 3, 4))
                                .build(),
                        SonarQubeProject.builder()
                                .key("test-project-key-2")
                                .name("Test Project Name 2")
                                .url("https://example.com/test-project-key-2")
                                .lastCommitTimestamp(LocalDateTime.of(2020, 6, 1, 1, 2, 3, 4))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-last-commit-age")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.HIGH)
                        .message("At least one of the component's SonarQube project(s) have not been updated with the results of at least 1 commit in the last 7 months. Is the component's build job still configured to send metrics to the SonarQube server? If not, the project(s) metrics in Kronicle will be out-of-date. \n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) was last updated with a commit 5 months ago\n"
                                + "* [test-project-key-2](https://example.com/test-project-key-2) was last updated with a commit 7 months ago")
                        .build());
    }

    @Test
    public void testShouldReturnPassWhenComponentHasOneSonarQubeProjectWithLastCommitAgeThatIsEqualToMaximum() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .lastCommitTimestamp(LocalDateTime.of(2020, 7, 1, 1, 2, 3, 4))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-last-commit-age")
                        .outcome(TestOutcome.PASS)
                        .priority(Priority.HIGH)
                        .message("All the component's SonarQube project(s) have updated with the results of at least 1 commit in the last 6 months. \n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) was last updated with a commit 6 months ago")
                        .build());
    }

    @Test
    public void testShouldReturnPassWhenComponentHasOneSonarQubeProjectWithLastCommitAgeThatIsLowerThanMaximum() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .lastCommitTimestamp(LocalDateTime.of(2020, 8, 1, 1, 2, 3, 4))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-last-commit-age")
                        .outcome(TestOutcome.PASS)
                        .priority(Priority.HIGH)
                        .message("All the component's SonarQube project(s) have updated with the results of at least 1 commit in the last 6 months. \n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) was last updated with a commit 5 months ago")
                        .build());
    }

    @Test
    public void testShouldReturnPassWhenComponentHasTwoSonarQubeProjectsWithLastCommitAgeThatIsLowerThanMaximum() {
        // Given
        Component component = Component.builder()
                .sonarQubeProjects(List.of(
                        SonarQubeProject.builder()
                                .key("test-project-key-1")
                                .name("Test Project Name 1")
                                .url("https://example.com/test-project-key-1")
                                .lastCommitTimestamp(LocalDateTime.of(2020, 8, 1, 1, 2, 3, 4))
                                .build(),
                        SonarQubeProject.builder()
                                .key("test-project-key-2")
                                .name("Test Project Name 2")
                                .url("https://example.com/test-project-key-2")
                                .lastCommitTimestamp(LocalDateTime.of(2020, 8, 1, 0, 0, 0, 0))
                                .build()))
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("sonar-qube-last-commit-age")
                        .outcome(TestOutcome.PASS)
                        .priority(Priority.HIGH)
                        .message("All the component's SonarQube project(s) have updated with the results of at least 1 commit in the last 6 months. \n"
                                + "\n"
                                + "* [test-project-key-1](https://example.com/test-project-key-1) was last updated with a commit 5 months ago\n"
                                + "* [test-project-key-2](https://example.com/test-project-key-2) was last updated with a commit 5 months ago")
                        .build());
    }
}