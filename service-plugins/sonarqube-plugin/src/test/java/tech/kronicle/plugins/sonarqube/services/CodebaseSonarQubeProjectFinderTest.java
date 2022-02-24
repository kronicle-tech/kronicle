package tech.kronicle.plugins.sonarqube.services;

import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.plugins.sonarqube.models.Project;
import tech.kronicle.pluginutils.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.pluginutils.utils.FileUtils;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CodebaseSonarQubeProjectFinderTest extends BaseCodebaseScannerTest {

    private final CodebaseSonarQubeProjectFinder underTest = new CodebaseSonarQubeProjectFinder(new FileUtils(new AntStyleIgnoreFileLoader()));

    @Test
    public void findProjectsShouldHandleACodebaseWithNoProjectReferences() {
        // Given
        Path codebaseDir = getCodebaseDir("NoProjectReferences");

        // When
        List<Project> returnValue = underTest.findProjects(codebaseDir, createTestProjects());

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void findProjectsShouldHandleACodebaseWithOneProjectReference() {
        // Given
        Path codebaseDir = getCodebaseDir("OneProjectReference");

        // When
        List<Project> returnValue = underTest.findProjects(codebaseDir, createTestProjects());

        // Then
        assertThat(returnValue).containsExactly(
                new Project("test-project-key-1", "Test Project Name 1"));
    }

    @Test
    public void findProjectsShouldHandleACodebaseWithTwoProjectReferences() {
        // Given
        Path codebaseDir = getCodebaseDir("TwoProjectReferences");

        // When
        List<Project> returnValue = underTest.findProjects(codebaseDir, createTestProjects());

        // Then
        assertThat(returnValue).containsExactly(
                new Project("test-project-key-1", "Test Project Name 1"),
                new Project("test-project-key-2", "Test Project Name 2"));
    }

    @Test
    public void findProjectsShouldHandleACodebaseWithAnUnknownProjectReference() {
        // Given
        Path codebaseDir = getCodebaseDir("UnknownProjectReference");

        // When
        List<Project> returnValue = underTest.findProjects(codebaseDir, createTestProjects());

        // Then
        assertThat(returnValue).isEmpty();
    }

    private List<Project> createTestProjects() {
        return List.of(
                createTestProject(1),
                createTestProject(2));
    }

    private Project createTestProject(int number) {
        return new Project("test-project-key-" + number, "Test Project Name " + number);
    }
}
