package tech.kronicle.service.scanners.sonarqube.services;

import tech.kronicle.sdk.models.SummaryMissingComponent;
import tech.kronicle.service.scanners.sonarqube.models.Project;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SonarQubeMissingComponentCollatorTest {

    private static final String TEST_SCANNER_ID = "test-scanner-id";

    private final SonarQubeMissingComponentCollator underTest = new SonarQubeMissingComponentCollator();

    @Test
    public void getMissingComponentsShouldReturnAnEmptyListWhenThereAreNoKnownSonarQubeProjects() {
        // Given
        List<Project> projects = List.of();
        Set<String> usedProjectKeys = Set.of();

        // When
        List<SummaryMissingComponent> missingComponents = underTest.getMissingComponents(TEST_SCANNER_ID, projects, usedProjectKeys);

        // Then
        assertThat(missingComponents).isEmpty();
    }

    @Test
    public void getMissingComponentsShouldReturnAnEmptyListWhenAllKnownSonarQubeProjectsHaveBeenUsedByKnownComponents() {
        // Given
        List<Project> projects = List.of(
                createTestProject(1),
                createTestProject(2));
        Set<String> usedProjectKeys = Set.of(
                createTestProjectKey(1),
                createTestProjectKey(2));

        // When
        List<SummaryMissingComponent> missingComponents = underTest.getMissingComponents(TEST_SCANNER_ID, projects, usedProjectKeys);

        // Then
        assertThat(missingComponents).isEmpty();
    }

    @Test
    public void getMissingComponentsShouldReturnMissingComponentsForMissingProjectsWhenSomeKnownSonarQubeProjectsAreNotUsedByKnownComponents() {
        // Given
        List<Project> projects = List.of(
                createTestProject(1),
                createTestProject(2),
                createTestProject(3),
                createTestProject(4));
        Set<String> usedProjectKeys = Set.of(
                createTestProjectKey(1),
                createTestProjectKey(3));

        // When
        List<SummaryMissingComponent> missingComponents = underTest.getMissingComponents(TEST_SCANNER_ID, projects, usedProjectKeys);

        // Then
        assertThat(missingComponents).containsExactly(
                createTestMissingComponent(2),
                createTestMissingComponent(4));
    }

    private Project createTestProject(int number) {
        return new Project(createTestProjectKey(number), "Test Project Name " + number);
    }

    private String createTestProjectKey(int number) {
        return "test-project-key-" + number;
    }

    private SummaryMissingComponent createTestMissingComponent(int number) {
        return SummaryMissingComponent.builder()
                .id(createTestProjectKey(number))
                .scannerId(TEST_SCANNER_ID)
                .build();
    }
}