package tech.kronicle.plugins.sonarqube;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.sonarqube.services.SonarQubeService;
import tech.kronicle.plugintestutils.scanners.BaseScannerTest;
import tech.kronicle.sdk.models.*;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.sdk.models.sonarqube.SummarySonarQubeMetric;

import jakarta.validation.Valid;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SonarQubeScannerTest extends BaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    @Mock
    private SonarQubeService mockService;
    private SonarQubeScanner underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new SonarQubeScanner(mockService);
    }

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("sonarqube");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Scans a component's codebase looking for any references to SonarQube project keys.  For any references it finds, it will call the SonarQube "
                + "server's API to retrieve all the latest metrics for those SonarQube projects");
    }

    @Test
    public void notesShouldReturnNull() {
        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void refreshShouldRefreshTheService() {
        // When
        underTest.refresh(ComponentMetadata.builder().build());

        // Then
        verify(mockService).refresh();
    }

    @Test
    public void scanShouldFindSonarQubeProjectsReferencedInCodebase() {
        // Given
        Path codebaseDir = Path.of("test-path");
        List<SonarQubeProject> projects = List.of(
                SonarQubeProject.builder().key("test-project-key-1").build(),
                SonarQubeProject.builder().key("test-project-key-2").build());
        when(mockService.findProjects(codebaseDir)).thenReturn(projects);

        // When
        Output<Void, Component> returnValue = underTest.scan(new ComponentAndCodebase(Component.builder().build(), new Codebase(new RepoReference("https://example.com"), codebaseDir)));

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        assertThat(getSonarQubeProjects(returnValue)).isEqualTo(projects);
    }

    @Test
    public void transformSummaryShouldAddMetricsAndMissingComponents() {
        // Given
        List<SummarySonarQubeMetric> metrics = List.of(
                SummarySonarQubeMetric.builder().key("test-metric-key-1").build(),
                SummarySonarQubeMetric.builder().key("test-metric-key-2").build());
        when(mockService.getMetrics()).thenReturn(metrics);
        SummaryMissingComponent missingComponent1 = SummaryMissingComponent.builder().id("test-missing-component-1").build();
        SummaryMissingComponent missingComponent2 = SummaryMissingComponent.builder().id("test-missing-component-2").build();
        when(mockService.getMissingComponents("sonarqube")).thenReturn(List.of(
                missingComponent1,
                missingComponent2));

        // When
        Summary returnValue = underTest.transformSummary(Summary.EMPTY);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getSonarQubeMetrics()).isSameAs(metrics);
        assertThat(returnValue.getMissingComponents()).containsExactly(
                missingComponent1,
                missingComponent2);
    }

    @Test
    public void transformSummaryShouldAddToAnyExistingMissingComponents() {
        // Given
        when(mockService.getMetrics()).thenReturn(List.of());
        SummaryMissingComponent missingComponent1 = SummaryMissingComponent.builder().id("test-missing-component-1").build();
        SummaryMissingComponent missingComponent2 = SummaryMissingComponent.builder().id("test-missing-component-2").build();
        SummaryMissingComponent missingComponent3 = SummaryMissingComponent.builder().id("test-missing-component-3").build();
        SummaryMissingComponent missingComponent4 = SummaryMissingComponent.builder().id("test-missing-component-4").build();
        when(mockService.getMissingComponents("sonarqube")).thenReturn(List.of(
                missingComponent3,
                missingComponent4));
        Summary summary = Summary.builder()
                .missingComponents(List.of(missingComponent1, missingComponent2))
                .build();

        // When
        Summary returnValue = underTest.transformSummary(summary);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getMissingComponents()).containsExactly(
                missingComponent1,
                missingComponent2,
                missingComponent3,
                missingComponent4);
    }

    private List<@Valid SonarQubeProject> getSonarQubeProjects(Output<Void, Component> returnValue) {
        SonarQubeProjectsState state = getMutatedComponent(returnValue).getState(SonarQubeProjectsState.TYPE);
        assertThat(state).isNotNull();
        return state.getSonarQubeProjects();
    }
}