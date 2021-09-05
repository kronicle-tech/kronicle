package tech.kronicle.service.scanners.sonarqube.services;

import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SonarQubeProjectCacheTest {

    private final SonarQubeProjectCache underTest = new SonarQubeProjectCache();

    @Test
    public void putShouldAddAProjectListToTheCache() {
        // Given
        Path codebaseDir = Path.of("test-path");
        List<SonarQubeProject> projects = List.of(
                SonarQubeProject.builder().key("test-project-key-1").build(),
                SonarQubeProject.builder().key("test-project-key-2").build());

        // When
        underTest.put(codebaseDir, projects);

        // Then
        List<SonarQubeProject> returnValue = underTest.get(codebaseDir);
        assertThat(returnValue).isSameAs(projects);
    }

    @Test
    public void putShouldAddAnEmptyProjectListToTheCache() {
        // Given
        Path codebaseDir = Path.of("test-path");
        List<SonarQubeProject> projects = List.of();

        // When
        underTest.put(codebaseDir, projects);

        // Then
        List<SonarQubeProject> returnValue = underTest.get(codebaseDir);
        assertThat(returnValue).isSameAs(projects);
    }

    @Test
    public void putShouldReplaceAnExistingProjectListInTheCache() {
        // Given
        Path codebaseDir = Path.of("test-path");
        List<SonarQubeProject> projects = List.of(
                SonarQubeProject.builder().key("test-project-key-1").build(),
                SonarQubeProject.builder().key("test-project-key-2").build());
        underTest.put(codebaseDir, projects);
        List<SonarQubeProject> projects2 = List.of(
                SonarQubeProject.builder().key("test-project-key-3").build(),
                SonarQubeProject.builder().key("test-project-key-4").build());

        // When
        underTest.put(codebaseDir, projects2);

        // Then
        List<SonarQubeProject> returnValue = underTest.get(codebaseDir);
        assertThat(returnValue).isSameAs(projects2);
    }

    @Test
    public void getShouldReturnNullWhenNotAlreadyInTheCache() {
        // Given
        Path codebaseDir = Path.of("test-path");

        // When
        underTest.get(codebaseDir);

        // Then
        List<SonarQubeProject> returnValue = underTest.get(codebaseDir);
        assertThat(returnValue).isNull();
    }

    @Test
    public void clearShouldClearTheCache() {
        // Given
        Path codebaseDir = Path.of("test-path");
        List<SonarQubeProject> projects = List.of(
                SonarQubeProject.builder().key("test-project-key-1").build(),
                SonarQubeProject.builder().key("test-project-key-2").build());
        underTest.put(codebaseDir, projects);

        // Then
        List<SonarQubeProject> returnValue = underTest.get(codebaseDir);
        assertThat(returnValue).isSameAs(projects);

        // When
        underTest.clear();

        // Then
        returnValue = underTest.get(codebaseDir);
        assertThat(returnValue).isNull();
    }

    @Test
    public void getUsedProjectKeysShouldReturnAnEmptyListWhenTheCacheIsEmpty() {
        // When
        Set<String> usedProjectKeys = underTest.getUsedProjectKeys();

        // When
        assertThat(usedProjectKeys).isEmpty();
    }


    @Test
    public void getUsedProjectKeysShouldReturnTheUsedProjectKeysWhenTheCacheIsNotEmpty() {
        // Given
        Path codebaseDir1 = Path.of("test-path-1");
        List<SonarQubeProject> projects1 = List.of(
                SonarQubeProject.builder().key("test-project-key-1").build(),
                SonarQubeProject.builder().key("test-project-key-2").build());
        Path codebaseDir2 = Path.of("test-path-2");
        List<SonarQubeProject> projects2 = List.of(
                SonarQubeProject.builder().key("test-project-key-3").build(),
                SonarQubeProject.builder().key("test-project-key-4").build());
        underTest.put(codebaseDir1, projects1);
        underTest.put(codebaseDir2, projects2);

        // When
        Set<String> usedProjectKeys = underTest.getUsedProjectKeys();

        // When
        assertThat(usedProjectKeys).containsExactlyInAnyOrder(
                "test-project-key-1",
                "test-project-key-2",
                "test-project-key-3",
                "test-project-key-4");
    }
}
