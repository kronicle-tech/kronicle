package com.moneysupermarket.componentcatalog.service.tests.sonarqube;

import com.moneysupermarket.componentcatalog.sdk.models.Component;
import com.moneysupermarket.componentcatalog.sdk.models.Priority;
import com.moneysupermarket.componentcatalog.sdk.models.TestResult;
import com.moneysupermarket.componentcatalog.sdk.models.sonarqube.SonarQubeProject;
import com.moneysupermarket.componentcatalog.service.scanners.sonarqube.config.SonarQubeConfig;
import com.moneysupermarket.componentcatalog.service.spring.stereotypes.Test;
import com.moneysupermarket.componentcatalog.service.tests.models.TestContext;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Test
@RequiredArgsConstructor
public class SonarQubeTest extends BaseSonarQubeTest {

    private final SonarQubeConfig config;

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
    protected TestResult doTest(Component input, TestContext testContext) {
        if (nonNull(input.getSonarQubeProjects()) && !input.getSonarQubeProjects().isEmpty()) {
            return createPassTestResult(createMessage(
                    "Component has these SonarQube project(s):",
                    createProjectOutcomes(input.getSonarQubeProjects()),
                    null));
        }

        if (componentTypeIdNotExpectedToUseSonarQube(input.getTypeId())) {
            return createNotApplicableTestResult(String.format("Component type %s is not expected to have a SonarQube project", input.getTypeId()));
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
