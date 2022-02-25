package tech.kronicle.service.tests.lastcommitage;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestOutcome;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.sdk.models.git.GitRepo;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

public class LastCommitAgeTestTest {

    private static final Clock clock = Clock.fixed(Instant.parse("2021-01-01T01:02:03.004Z"), ZoneOffset.UTC);
    private final LastCommitAgeTest underTest = new LastCommitAgeTest(clock);

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheTest() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Checks whether there has been at least 1 code commit to a component's repo is the last 6 months");
    }

    @Test
    public void priorityShouldReturnHigh() {
        // When
        Priority returnValue = underTest.priority();

        // Then
        assertThat(returnValue).isEqualTo(Priority.HIGH);
    }

    @Test
    public void testShouldReturnNotApplicableWhenComponentGitRepoIsNull() {
        // Given
        Component component = Component.builder().build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("last-commit-age")
                        .outcome(TestOutcome.NOT_APPLICABLE)
                        .priority(Priority.HIGH)
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenLastCommitAgeIsOver6Months() {
        // Given
        Component component = Component.builder()
                .gitRepo(GitRepo.builder()
                        .lastCommitTimestamp(LocalDateTime.of(2020, 1, 1, 1, 2, 3, 4))
                        .build())
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("last-commit-age")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.HIGH)
                        .message("There has not been any code commits to the component's repo in the last 12 months.  Will the component's build and deployment\n"
                                + "jobs still work next time there is a code commit?  Could there be vulnerabilities in the component that have not been found\n"
                                + "because a build has not been ran?  ")
                        .build());
    }

    @Test
    public void testShouldReturnFailWhenLastCommitAgeIs6Months() {
        // Given
        Component component = Component.builder()
                .gitRepo(GitRepo.builder()
                        .lastCommitTimestamp(LocalDateTime.of(2020, 7, 1, 1, 2, 3, 4))
                        .build())
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("last-commit-age")
                        .outcome(TestOutcome.FAIL)
                        .priority(Priority.HIGH)
                        .message("There has not been any code commits to the component's repo in the last 6 months.  Will the component's build and deployment\n"
                                + "jobs still work next time there is a code commit?  Could there be vulnerabilities in the component that have not been found\n"
                                + "because a build has not been ran?  ")
                        .build());
    }

    @Test
    public void testShouldReturnPassWhenLastCommitAgeIsLessThan6Months() {
        // Given
        Component component = Component.builder()
                .gitRepo(GitRepo.builder()
                        .lastCommitTimestamp(LocalDateTime.of(2020, 8, 1, 1, 2, 3, 4))
                        .build())
                .build();

        // When
        TestResult returnValue = underTest.test(component, null);

        // Then
        assertThat(returnValue).isEqualTo(
                TestResult.builder()
                        .testId("last-commit-age")
                        .outcome(TestOutcome.PASS)
                        .priority(Priority.HIGH)
                        .message("It has only been 5 months since the last code commit to the component's repo.  ")
                        .build());
    }
}
