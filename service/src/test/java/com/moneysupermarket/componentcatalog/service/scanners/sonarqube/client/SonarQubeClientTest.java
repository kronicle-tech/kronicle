package com.moneysupermarket.componentcatalog.service.scanners.sonarqube.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.moneysupermarket.componentcatalog.sdk.models.sonarqube.SonarQubeMeasure;
import com.moneysupermarket.componentcatalog.sdk.models.sonarqube.SummarySonarQubeMetric;
import com.moneysupermarket.componentcatalog.service.scanners.sonarqube.config.SonarQubeConfig;
import com.moneysupermarket.componentcatalog.service.scanners.sonarqube.models.Project;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"sonarqube.base-url=http://localhost:36202"})
@ContextConfiguration(classes = { SonarQubeClientTestConfiguration.class})
@EnableConfigurationProperties(value = { SonarQubeConfig.class})
public class SonarQubeClientTest {

    @Autowired
    private SonarQubeClient underTest;
    private WireMockServer wireMockServer;

    @AfterEach
    public void afterEach() {
        wireMockServer.stop();
    }

    @Test
    public void getMetricsShouldReturnAllMetrics() {
        // Given
        wireMockServer = SonarQubeWireMockFactory.createWithRealResponses();

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
    public void getProjectsShouldReturnAllProjects() {
        // Given
        wireMockServer = SonarQubeWireMockFactory.createWithRealResponses();

        // When
        List<Project> returnValue = underTest.getProjects();

        // Then
        assertThat(returnValue).hasSize(105);
        IntStream.range(1, 106).forEach(projectNumber -> assertThat(returnValue).contains(
                new Project("test-component-key-" + projectNumber, "Test Component Name " + projectNumber)));
    }

    @Test
    public void getProjectMeasuresShouldReturnAllMeasuresForSpecifiedMetricsForAProject() {
        // Given
        wireMockServer = SonarQubeWireMockFactory.createWithRealResponses();

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