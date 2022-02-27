package tech.kronicle.service.tests.sonarqube;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.service.tests.ComponentTest;
import tech.kronicle.service.tests.models.TestContext;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static tech.kronicle.pluginutils.MarkdownHelper.createMarkdownLink;

@RequiredArgsConstructor
public abstract class BaseSonarQubeTest extends ComponentTest {

    protected abstract boolean ignoreNoProjects();

    @Override
    public final TestResult test(Component input, TestContext testContext) {
        if (ignoreNoProjects() && hasNoProjects(input)) {
            return createNotApplicableTestResult("Component does not have a SonarQube project");
        }

        return doTest(input, testContext);
    }

    private boolean hasNoProjects(Component input) {
        return isNull(input.getSonarQubeProjects()) || input.getSonarQubeProjects().isEmpty();
    }

    protected abstract TestResult doTest(Component input, TestContext testContext);

    protected <T> String createMessage(String start, List<ProjectOutcome<T>> projectOutcomes, Function<ProjectOutcome<T>, String> projectMessageSupplier) {
        return start
                + "\n\n"
                + projectOutcomes.stream().map(createMessage(projectMessageSupplier)).collect(Collectors.joining("\n"));
    }

    private <T> Function<ProjectOutcome<T>, String> createMessage(Function<ProjectOutcome<T>, String> projectMessageSupplier) {
        return projectOutcome -> {
            StringBuilder messageBuilder = new StringBuilder()
                    .append("* ")
                    .append(createProjectMarkdownLink(projectOutcome.getProject()));
            if (nonNull(projectMessageSupplier)) {
                messageBuilder.append(" ")
                        .append(projectMessageSupplier.apply(projectOutcome));
            }
            return messageBuilder.toString();
        };
    }

    private String createProjectMarkdownLink(SonarQubeProject project) {
        return createMarkdownLink(project.getKey(), project.getUrl());
    }

    @Value
    protected static class ProjectOutcome<T> {

        SonarQubeProject project;
        T outcome;
    }
}
