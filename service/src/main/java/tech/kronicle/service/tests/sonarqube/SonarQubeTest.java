package tech.kronicle.service.tests.sonarqube;

import lombok.RequiredArgsConstructor;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Priority;
import tech.kronicle.sdk.models.TestResult;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.service.tests.models.TestContext;
import tech.kronicle.service.tests.sonarqube.config.SonarQubeTestConfig;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class SonarQubeTest extends BaseSonarQubeTest {

    private final SonarQubeTestConfig config;

    @Override
    public String description() {
        return "Checks whether a component has an associated SonarQube project";
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    protected boolean ignoreNoProjects() {
        return false;
    }

    @Override
    protected TestResult doTest(Component input, List<SonarQubeProject> sonarQubeProjects, TestContext testContext) {
        if (!sonarQubeProjects.isEmpty()) {
            return createPassTestResult(createMessage(
                    "Component has these SonarQube project(s):",
                    createProjectOutcomes(sonarQubeProjects),
                    null));
        }

        if (componentTypeIdNotExpectedToUseSonarQube(input.getType())) {
            return createNotApplicableTestResult(String.format("Component type %s is not expected to have a SonarQube project", input.getType()));
        }

        return createFailTestResult(
                "Component does not have a SonarQube project.  The SonarQube Scanner auto detects which SonarQube projects a component "
                + "has by search its codebase for any files that contain a line of text that contains both the word `sonar` and the key of any SonarQube "
                + "project defined on the SonarQube server.  For example, the scanner would detect a SonarQube project key from this: \n"
                + "\n"
                + "```\n"
                + "property 'sonar.projectKey', 'com.example:some-project'\n"
                + "```");
    }

    private List<ProjectOutcome<Void>> createProjectOutcomes(List<SonarQubeProject> projects) {
        return projects.stream()
                .map(project -> new ProjectOutcome<Void>(project, null))
                .collect(Collectors.toList());
    }

    private boolean componentTypeIdNotExpectedToUseSonarQube(String componentTypeId) {
        return isNull(config.getExpectedComponentTypeIds()) || !config.getExpectedComponentTypeIds().contains(componentTypeId);
    }
}
