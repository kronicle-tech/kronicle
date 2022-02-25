package tech.kronicle.plugins.sonarqube.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.kronicle.plugins.sonarqube.models.Project;
import tech.kronicle.sdk.models.sonarqube.SonarQubeMeasure;
import tech.kronicle.sdk.models.sonarqube.SummarySonarQubeMetric;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SonarQubeClientTestConfiguration.class})
public class SonarQubeClientTest {

    @Autowired
    private SonarQubeClient underTest;
    private WireMockServer wireMockServer;
    private final SonarQubeWireMockFactory sonarQubeWireMockFactory = new SonarQubeWireMockFactory();

    @AfterEach
    public void afterEach() {
        wireMockServer.stop();
    }

    @Test
    public void getMetricsShouldReturnAllMetrics() {
        // Given
        wireMockServer = sonarQubeWireMockFactory.createWithRealResponses();

        // When
        List<SummarySonarQubeMetric> returnValue = underTest.getMetrics();

        // Then
        assertThat(returnValue).hasSize(105);
        IntStream.range(1, 106).forEach(metricNumber -> assertThat(returnValue).contains(SummarySonarQubeMetric.builder()
                .id(Integer.toString(1000 + metricNumber))
                .key("test-metric-key-" + metricNumber)
                .type("INT")
                .name("Test Metric Name " + metricNumber)
                .description("Test Metric Description " + metricNumber)
                .domain("Test Metric Domain " + metricNumber)
                .direction(metricNumber % 2 == 0 ? -1 : 1)
                .qualitative(metricNumber % 2 == 0)
                .hidden(false)
                .custom(false)
                .build()));
    }

    @Test
    public void getProjectsShouldReturnAllProjectsWhenNoOrganizationIsSpecified() {
        // Given
        wireMockServer = sonarQubeWireMockFactory.createWithRealResponses();
        String organization = null;

        // When
        List<Project> returnValue = underTest.getProjects(organization);

        // Then
        assertThat(returnValue).hasSize(105);
        List<Project> expectedProjects = IntStream.range(1, 106)
                .mapToObj(projectNumber -> new Project("test-component-key-" + projectNumber, "Test Component Name " + projectNumber + " with no organization"))
                .collect(Collectors.toList());
        assertThat(returnValue).containsExactlyElementsOf(expectedProjects);
    }

    @Test
    public void getProjectsShouldReturnAllProjectsWhenAnOrganizationIsSpecified() {
        // Given
        wireMockServer = sonarQubeWireMockFactory.createWithRealResponses();
        String organization = "test-organization";

        // When
        List<Project> returnValue = underTest.getProjects(organization);

        // Then
        assertThat(returnValue).hasSize(105);
        List<Project> expectedProjects = IntStream.range(1, 106)
                .mapToObj(projectNumber -> new Project("test-component-key-" + projectNumber, "Test Component Name " + projectNumber + " with organization test-organization"))
                .collect(Collectors.toList());
        assertThat(returnValue).containsExactlyElementsOf(expectedProjects);
    }

    @Test
    public void getProjectMeasuresShouldReturnAllMeasuresForSpecifiedMetricsForAProject() {
        // Given
        wireMockServer = sonarQubeWireMockFactory.createWithRealResponses();

        // When
        List<SonarQubeMeasure> returnValue = underTest.getProjectMeasures("test-component-key-1", List.of(
                SummarySonarQubeMetric.builder().key("test-metric-key-1").build(),
                SummarySonarQubeMetric.builder().key("test-metric-key-2").build()));

        // Then
        assertThat(returnValue).hasSize(2);
        IntStream.range(1, 3).forEach(measureNumber -> assertThat(returnValue).contains(
                SonarQubeMeasure.builder()
                        .metric("test-metric-key-" + measureNumber)
                        .value(Integer.toString(measureNumber))
                        .bestValue(false)
                        .build()));
    }
}