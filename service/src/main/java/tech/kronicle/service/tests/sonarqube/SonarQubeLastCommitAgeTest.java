package tech.kronicle.service.tests.sonarqube;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.service.tests.models.TestContext;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class SonarQubeLastCommitAgeTest extends BaseSonarQubeTest {

    private static final int MAX_MONTHS = 6;

    private final Clock clock;

    @Override
    public String description() {
        return String.format("Checks whether a SonarQube project has received an update in the last %d months", MAX_MONTHS);
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    protected boolean ignoreNoProjects() {
        return true;
    }

    @Override
    public TestResult doTest(Component input, TestContext testContext) {
        List<ProjectOutcome<Outcome>> projectOutcomes = input.getSonarQubeProjects().stream()
                .map(this::createProjectOutcome)
                .collect(Collectors.toList());
        List<Period> lastCommitAges = getLastCommitAges(projectOutcomes);

        if (anyProjectHasNoLastCommitAge(lastCommitAges)) {
            return createFailTestResult(createMessage(
                    "At least one of the component's SonarQube projects has no last commit date metric:",
                    projectOutcomes,
                    this::formatProjectOutcomeMessage));
        }
        int maxLastCommitAgeInMonths = getMaxLastCommitAgeInMonths(lastCommitAges);
        if (maxLastCommitAgeInMonths > MAX_MONTHS) {
            return createFailTestResult(createMessage(
                    String.format(
                            "At least one of the component's SonarQube project(s) have not been updated with the results of at least 1 commit in the last %d "
                            + "months. Is the component's build job still configured to send metrics to the SonarQube server? If not, the project(s) metrics "
                            + "in Kronicle will be out-of-date. ",
                            maxLastCommitAgeInMonths),
                    projectOutcomes,
                    this::formatProjectOutcomeMessage));
        }
        return createPassTestResult(createMessage(
                String.format(
                        "All the component's SonarQube project(s) have updated with the results of at least 1 commit in the last %d months. ",
                        MAX_MONTHS),
                projectOutcomes,
                this::formatProjectOutcomeMessage));
    }

    private ProjectOutcome<Outcome> createProjectOutcome(SonarQubeProject project) {
        return new ProjectOutcome<>(project, new Outcome(getLastCommitAge(project.getLastCommitTimestamp())));
    }

    private Period getLastCommitAge(LocalDateTime lastCommitTimestamp) {
        if (isNull(lastCommitTimestamp)) {
            return null;
        }
        return Period.between(lastCommitTimestamp.toLocalDate(), LocalDate.now(clock));
    }

    private List<Period> getLastCommitAges(List<ProjectOutcome<Outcome>> projectOutcomes) {
        return projectOutcomes.stream()
                .map(ProjectOutcome::getOutcome)
                .map(Outcome::getLastCommitAge)
                .collect(Collectors.toList());
    }

    private int getMaxLastCommitAgeInMonths(List<Period> lastCommitAges) {
        return (int) lastCommitAges.stream()
                .mapToLong(Period::toTotalMonths)
                .max()
                .getAsLong();
    }

    private boolean anyProjectHasNoLastCommitAge(List<Period> lastCommitAges) {
        return lastCommitAges.stream()
                .anyMatch(Objects::isNull);
    }

    private String formatProjectOutcomeMessage(ProjectOutcome<Outcome> projectOutcome) {
        Period lastCommitAge = projectOutcome.getOutcome().getLastCommitAge();
        return nonNull(lastCommitAge)
                ? String.format("was last updated with a commit %d months ago", lastCommitAge.toTotalMonths())
                : "does not have a last commit date";
    }

    @Value
    private static class Outcome {

        Period lastCommitAge;
    }
}
