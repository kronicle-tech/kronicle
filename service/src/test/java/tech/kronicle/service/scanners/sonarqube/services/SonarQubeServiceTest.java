package tech.kronicle.service.scanners.sonarqube.services;

import tech.kronicle.sdk.models.SummaryMissingComponent;
import tech.kronicle.sdk.models.sonarqube.SonarQubeMeasure;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.sdk.models.sonarqube.SummarySonarQubeMetric;
import tech.kronicle.service.scanners.sonarqube.client.SonarQubeClient;
import tech.kronicle.service.scanners.sonarqube.models.Project;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
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
        underTest = new SonarQubeService(mockProjectFinder, mockClient, mockProjectCache, mockProjectCreator, mockMissingComponentCollator);    }

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
        when(mockClient.getProjects()).thenReturn(projects1);
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
        when(mockClient.getProjects()).thenReturn(projects2);
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
        when(mockClient.getProjects()).thenReturn(projects);
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
        when(mockClient.getProjects()).thenReturn(projects);
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
        when(mockClient.getProjects()).thenReturn(projects);
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
}