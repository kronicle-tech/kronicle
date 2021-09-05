package tech.kronicle.service.tests.sonarqube;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.service.spring.stereotypes.Test;
import tech.kronicle.service.tests.models.TestContext;
import lombok.Value;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Test
public class SonarQubeCodeCoverageTest extends BaseSonarQubeTest {

    private static final String COVERAGE_METRIC_KEY = "coverage";
    public static final int MIN_CODE_COVERAGE = 80;

    @Override
    public String description() {
        return "Checks whether a component has at least 80% unit test code coverage";
    }

    @Override
    public Priority priority() {
        return Priority.VERY_HIGH;
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

        if (isAnyProjectWithNoCodeCoverageMetric(projectOutcomes)) {
            return createFailTestResult(createMessage(
                    "At least one SonarQube project does not include a code coverage metric:",
                    projectOutcomes,
                    this::formatProjectOutcomeMessage));
        }

        if (isAnyProjectWithCodeCoverageBelowMinimum(projectOutcomes)) {
            return createFailTestResult(createMessage(
                    "At least one SonarQube project has code coverage below the minimum:",
                    projectOutcomes,
                    this::formatProjectOutcomeMessage));
        }

        return createPassTestResult(createMessage(
                "All SonarQube project(s) have unit test code coverage that satisfies the minimum:",
                    projectOutcomes,
                    this::formatProjectOutcomeMessage));
    }

    private ProjectOutcome<Outcome> createProjectOutcome(SonarQubeProject project) {
        Double codeCoverage = getCodeCoverage(project);
        return new ProjectOutcome<>(project, new Outcome(codeCoverage));
    }

    private Double getCodeCoverage(SonarQubeProject project) {
        return project.getMeasures().stream()
                .filter(measure -> Objects.equals(measure.getMetric(), COVERAGE_METRIC_KEY))
                .findFirst()
                .map(coverageMeasure -> Double.parseDouble(coverageMeasure.getValue()))
                .orElse(null);
    }

    private boolean isAnyProjectWithNoCodeCoverageMetric(List<ProjectOutcome<Outcome>> projectOutcomes) {
        return projectOutcomes.stream().anyMatch(projectOutcome -> isNull(projectOutcome.getOutcome().getCodeCoverage()));
    }

    private boolean isAnyProjectWithCodeCoverageBelowMinimum(List<ProjectOutcome<Outcome>> projectOutcomes) {
        return projectOutcomes.stream().anyMatch(projectOutcome -> projectOutcome.getOutcome().getCodeCoverage() < MIN_CODE_COVERAGE);
    }

    private String formatProjectOutcomeMessage(ProjectOutcome<Outcome> projectOutcome) {
        Double codeCoverage = projectOutcome.getOutcome().getCodeCoverage();
        return nonNull(codeCoverage)
                ? String.format("has %.1f%% unit test code coverage", codeCoverage)
                : "does not have a code coverage metric";
    }

    @Value
    private static class Outcome {

        Double codeCoverage;
    }
}
