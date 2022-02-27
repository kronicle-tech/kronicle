package tech.kronicle.plugins.sonarqube.services;

import lombok.Value;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.sonarqube.client.SonarQubeClient;
import tech.kronicle.plugins.sonarqube.config.SonarQubeConfig;
import tech.kronicle.plugins.sonarqube.models.Project;
import tech.kronicle.sdk.models.SummaryMissingComponent;
import tech.kronicle.sdk.models.sonarqube.SonarQubeMeasure;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.sdk.models.sonarqube.SummarySonarQubeMetric;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SonarQubeServiceTest {

    @Mock
    private CodebaseSonarQubeProjectFinder mockProjectFinder;
    @Mock
    private SonarQubeClient mockClient;
    @Mock
    private SonarQubeProjectCache mockProjectCache;
    @Mock
    private SonarQubeProjectCreator mockProjectCreator;
    @Mock
    private SonarQubeMissingComponentCollator mockMissingComponentCollator;
    private SonarQubeService underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = createUnderTest(new SonarQubeConfig(null, null, null));
    }

    @Test
    public void refreshShouldClearTheProjectCache() {
        // When
        underTest.refresh();

        // Then
        verify(mockProjectCache).clear();
    }

    @Test
    public void refreshShouldRetrieveFreshCopiesOfTheMetricsAndTheProjects() {
        // Given
        Path codebaseDir = Path.of("test-path");
        List<SummarySonarQubeMetric> metrics1 = List.of(
                SummarySonarQubeMetric.builder().key("test-metric-key-1").build(),
                SummarySonarQubeMetric.builder().key("test-metric-key-2").build());
        when(mockClient.getMetrics()).thenReturn(metrics1);
        Project project1 = new Project("test-project-key-1", "Test Project Name 1");
        Project project2 = new Project("test-project-key-2", "Test Project Name 2");
        List<Project> projects1 = List.of(project1, project2);
        when(mockProjectCache.get(codebaseDir)).thenReturn(null);
        when(mockClient.getProjects(null)).thenReturn(projects1);
        when(mockProjectFinder.findProjects(codebaseDir, projects1)).thenReturn(List.of(project1));

        // Then
        Assertions.assertThat(underTest.getMetrics()).isNull();

        // When
        underTest.refresh();

        // Then
        Assertions.assertThat(underTest.getMetrics()).isSameAs(metrics1);

        // When
        underTest.findProjects(codebaseDir);

        // Then
        verify(mockProjectFinder).findProjects(codebaseDir, projects1);
        verify(mockClient).getProjectMeasures("test-project-key-1", metrics1);

        // Given
        List<SummarySonarQubeMetric> metrics2 = List.of(
                SummarySonarQubeMetric.builder().key("test-metric-key-3").build(),
                SummarySonarQubeMetric.builder().key("test-metric-key-4").build());
        when(mockClient.getMetrics()).thenReturn(metrics2);
        Project project3 = new Project("test-project-key-3", "Test Project Name 3");
        Project project4 = new Project("test-project-key-4", "Test Project Name 4");
        List<Project> projects2 = List.of(project3, project4);
        when(mockClient.getProjects(null)).thenReturn(projects2);
        when(mockProjectFinder.findProjects(codebaseDir, projects2)).thenReturn(List.of(project3));

        // When
        underTest.refresh();

        // Then
        Assertions.assertThat(underTest.getMetrics()).isSameAs(metrics2);

        // When
        underTest.findProjects(codebaseDir);

        // Then
        verify(mockProjectFinder).findProjects(codebaseDir, projects2);
        verify(mockClient).getProjectMeasures("test-project-key-3", metrics2);
    }

    @TestFactory
    public Stream<DynamicTest> refreshShouldHandleNullOrganizationsConfig() {
        String organization1 = "test-organization-1";
        String organization2 = "test-organization-2";
        String scenarioDescriptionPrefix = "findProjects() should handle";

        return Stream.of(
                        new OrganizationScenario(scenarioDescriptionPrefix + " when organizations config is null", null),
                        new OrganizationScenario(scenarioDescriptionPrefix + " when organizations config is an empty list", List.of()),
                        new OrganizationScenario(scenarioDescriptionPrefix + " when organizations config contains multiple organizations", List.of(organization1, organization2)))
                .map(scenario -> dynamicTest(scenario.description, () -> {
                    // Given
                    SonarQubeService underTest = createUnderTest(createConfig(scenario.getOrganizations()));
                    Path codebaseDir1 = Path.of("test-path-1");
                    Path codebaseDir2 = Path.of("test-path-2");
                    when(mockProjectCache.get(codebaseDir1)).thenReturn(null);
                    when(mockProjectCache.get(codebaseDir2)).thenReturn(null);
                    List<SummarySonarQubeMetric> metrics = List.of(
                            SummarySonarQubeMetric.builder().key("test-metric-key-1").build(),
                            SummarySonarQubeMetric.builder().key("test-metric-key-2").build());
                    when(mockClient.getMetrics()).thenReturn(metrics);
                    Project project1 = new Project("test-project-key-1", "Test Project Name 1");
                    Project project2 = new Project("test-project-key-2", "Test Project Name 2");
                    Project project3 = new Project("test-project-key-3", "Test Project Name 3");
                    Project project4 = new Project("test-project-key-4", "Test Project Name 4");
                    List<Project> allProjects = List.of(project1, project2, project3, project4);

                    if (scenario.hasNoOrganizations()) {
                        when(mockClient.getProjects(null)).thenReturn(allProjects);
                    } else {
                        when(mockClient.getProjects(organization1)).thenReturn(List.of(project1, project2));
                        when(mockClient.getProjects(organization2)).thenReturn(List.of(project3, project4));
                    }

                    when(mockProjectFinder.findProjects(codebaseDir1, allProjects)).thenReturn(List.of(project1));
                    when(mockProjectFinder.findProjects(codebaseDir2, allProjects)).thenReturn(List.of(project3));
                    List<SonarQubeMeasure> project1Measures = List.of(
                            SonarQubeMeasure.builder().metric("test-metric-key-1").value("1-1").build(),
                            SonarQubeMeasure.builder().metric("test-metric-key-2").value("1-2").build());
                    when(mockClient.getProjectMeasures(project1.getKey(), metrics)).thenReturn(project1Measures);
                    List<SonarQubeMeasure> project3Measures = List.of(
                            SonarQubeMeasure.builder().metric("test-metric-key-1").value("3-1").build(),
                            SonarQubeMeasure.builder().metric("test-metric-key-2").value("3-2").build());
                    when(mockClient.getProjectMeasures(project3.getKey(), metrics)).thenReturn(project3Measures);
                    SonarQubeProject expectedProject1 = new SonarQubeProject("test-project-1", "Test Project 1", null, null, null);
                    when(mockProjectCreator.create(project1, project1Measures)).thenReturn(expectedProject1);
                    SonarQubeProject expectedProject3 = new SonarQubeProject("test-project-3", "Test Project 3", null, null, null);
                    when(mockProjectCreator.create(project3, project3Measures)).thenReturn(expectedProject3);

                    // When
                    underTest.refresh();
                    List<SonarQubeProject> returnValue1 = underTest.findProjects(codebaseDir1);
                    List<SonarQubeProject> returnValue2 = underTest.findProjects(codebaseDir2);

                    // Then
                    assertThat(returnValue1).containsExactly(expectedProject1);
                    assertThat(returnValue2).containsExactly(expectedProject3);
                    
                    
                }));
    }

    @Test
    public void findProjectsShouldFindProjectsInACodebase() {
        // Given
        List<SummarySonarQubeMetric> metrics = List.of(
                SummarySonarQubeMetric.builder().key("test-metric-key-1").build(),
                SummarySonarQubeMetric.builder().key("test-metric-key-2").build());
        when(mockClient.getMetrics()).thenReturn(metrics);
        Project project1 = new Project("test-project-key-1", "Test Project Name 1");
        Project project2 = new Project("test-project-key-2", "Test Project Name 2");
        Project project3 = new Project("test-project-key-3", "Test Project Name 3");
        List<Project> projects = List.of(project1, project2, project3);
        when(mockClient.getProjects(null)).thenReturn(projects);
        underTest.refresh();

        Path codebaseDir = Path.of("test-path");
        when(mockProjectCache.get(codebaseDir)).thenReturn(null);

        when(mockProjectFinder.findProjects(codebaseDir, projects)).thenReturn(List.of(project2, project3));

        List<SonarQubeMeasure> project2Measures = List.of(
                SonarQubeMeasure.builder().metric("test-metric-key-1").value("2-1").build(),
                SonarQubeMeasure.builder().metric("test-metric-key-2").value("2-2").build());
        when(mockClient.getProjectMeasures(project2.getKey(), metrics)).thenReturn(project2Measures);

        List<SonarQubeMeasure> project3Measures = List.of(
                SonarQubeMeasure.builder().metric("test-metric-key-1").value("3-1").build(),
                SonarQubeMeasure.builder().metric("test-metric-key-2").value("3-2").build());
        when(mockClient.getProjectMeasures(project3.getKey(), metrics)).thenReturn(project3Measures);

        SonarQubeProject codebaseProject2 = SonarQubeProject.builder()
                .key("test-project-key-2")
                .build();
        when(mockProjectCreator.create(project2, project2Measures)).thenReturn(codebaseProject2);

        SonarQubeProject codebaseProject3 = SonarQubeProject.builder()
                .key("test-project-key-3")
                .build();
        when(mockProjectCreator.create(project3, project3Measures)).thenReturn(codebaseProject3);

        // When
        List<SonarQubeProject> returnValue = underTest.findProjects(codebaseDir);

        // Then
        assertThat(returnValue).containsExactly(
                SonarQubeProject.builder()
                        .key("test-project-key-2")
                        .build(),
                SonarQubeProject.builder()
                        .key("test-project-key-3")
                        .build());
        verify(mockProjectCache).put(codebaseDir, returnValue);
    }

    @Test
    public void findProjectsShouldReturnTheListOfProjectsFromTheCacheIfPopulated() {
        // Given
        List<SummarySonarQubeMetric> metrics = List.of(
                SummarySonarQubeMetric.builder().key("test-metric-key-1").build(),
                SummarySonarQubeMetric.builder().key("test-metric-key-2").build());
        when(mockClient.getMetrics()).thenReturn(metrics);
        List<Project> projects = List.of(
                new Project("test-project-key-1", "Test Project Name 1"),
                new Project("test-project-key-2", "Test Project Name 2"),
                new Project("test-project-key-3", "Test Project Name 3"));
        when(mockClient.getProjects(null)).thenReturn(projects);
        underTest.refresh();

        Path codebaseDir = Path.of("test-path");
        SonarQubeProject codebaseProject2 = SonarQubeProject.builder()
                .key("test-project-key-2")
                .build();
        SonarQubeProject codebaseProject3 = SonarQubeProject.builder()
                .key("test-project-key-3")
                .build();
        List<SonarQubeProject> codebaseProjects = List.of(codebaseProject2, codebaseProject3);
        when(mockProjectCache.get(codebaseDir)).thenReturn(codebaseProjects);

        // When
        List<SonarQubeProject> returnValue = underTest.findProjects(codebaseDir);

        // Then
        assertThat(returnValue).isSameAs(codebaseProjects);
    }

    @Test
    public void getMissingComponentsShouldGetTheMissingComponents() {
        // Given
        List<Project> projects = List.of(
                new Project("test-project-key-1", "Test Project Name 1"),
                new Project("test-project-key-2", "Test Project Name 2"));
        when(mockClient.getProjects(null)).thenReturn(projects);
        underTest.refresh();

        Set<String> unusedProjectKeys = Set.of(
                "test-project-key-1",
                "test-project-key-2");
        when(mockProjectCache.getUsedProjectKeys()).thenReturn(unusedProjectKeys);

        SummaryMissingComponent missingComponent1 = SummaryMissingComponent.builder()
                .id("test-missing-component-id-1")
                .scannerId("test-scanner-id")
                .build();
        SummaryMissingComponent missingComponent2 = SummaryMissingComponent.builder()
                .id("test-missing-component-id-1")
                .scannerId("test-scanner-id")
                .build();
        String scannerId = "test-scanner-id";
        List<SummaryMissingComponent> missingComponents = List.of(missingComponent1, missingComponent2);
        when(mockMissingComponentCollator.getMissingComponents(scannerId, projects, unusedProjectKeys)).thenReturn(missingComponents);

        // When
        Collection<SummaryMissingComponent> returnValue = underTest.getMissingComponents(scannerId);
        assertThat(returnValue).isSameAs(missingComponents);
    }

    private SonarQubeService createUnderTest(SonarQubeConfig config) {
        return new SonarQubeService(
                config, mockClient, mockProjectFinder, mockProjectCache, mockProjectCreator, mockMissingComponentCollator);
    }

    private SonarQubeConfig createConfig(List<String> organizations) {
        return new SonarQubeConfig(null, null, organizations);
    }

    @Value
    private static class OrganizationScenario {

        String description;
        List<String> organizations;

        private boolean hasNoOrganizations() {
            return isNull(organizations) || organizations.isEmpty();
        }
    }
}