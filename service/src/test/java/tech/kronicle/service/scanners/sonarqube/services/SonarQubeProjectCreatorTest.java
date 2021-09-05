package tech.kronicle.service.scanners.sonarqube.services;

import tech.kronicle.sdk.models.sonarqube.SonarQubeMeasure;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.service.scanners.sonarqube.config.SonarQubeConfig;
import tech.kronicle.service.scanners.sonarqube.models.Project;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SonarQubeProjectCreatorTest {

    private static final String BASE_URL = "https://example.com/example";

    private final SonarQubeConfig config = new SonarQubeConfig(BASE_URL, null);
    private final SonarQubeProjectCreator underTest = new SonarQubeProjectCreator(config);

    @Test
    public void createShouldCreateASonarQubeProjectPojoWhenThereAreNoMeasures() {
        // Given
        Project project = new Project("test-project-key-1", "Test Project Name 1");
        List<SonarQubeMeasure> measures = List.of();

        // When
        SonarQubeProject returnValue = underTest.create(project, measures);

        // Then
        assertThat(returnValue).isEqualTo(SonarQubeProject.builder()
                .key("test-project-key-1")
                .name("Test Project Name 1")
                .url(BASE_URL + "/dashboard?id=test-project-key-1")
                .build());
    }

    @Test
    public void createShouldCreateASonarQubeProjectPojoWithMeasuresWhenThereAreMeasures() {
        // Given
        Project project = new Project("test-project-key-1", "Test Project Name 1");
        List<SonarQubeMeasure> measures = List.of(
                SonarQubeMeasure.builder().metric("test-metric-key-1").value("1").bestValue(false).build(),
                SonarQubeMeasure.builder().metric("test-metric-key-2").value("2").bestValue(false).build());

        // When
        SonarQubeProject returnValue = underTest.create(project, measures);

        // Then
        assertThat(returnValue).isEqualTo(SonarQubeProject.builder()
                .key("test-project-key-1")
                .name("Test Project Name 1")
                .url(BASE_URL + "/dashboard?id=test-project-key-1")
                .measures(measures)
                .build());
    }

    @Test
    public void createShouldCreateASonarQubeProjectPojoWithLastCommitTimestampWhenThereIsALastCommitTimestampMeasure() {
        // Given
        Project project = new Project("test-project-key-1", "Test Project Name 1");
        List<SonarQubeMeasure> measures = List.of(
                SonarQubeMeasure.builder().metric("test-metric-key-1").value("1").bestValue(false).build(),
                SonarQubeMeasure.builder().metric("last_commit_date").value("1609556645").bestValue(false).build(),
                SonarQubeMeasure.builder().metric("test-metric-key-2").value("2").bestValue(false).build());

        // When
        SonarQubeProject returnValue = underTest.create(project, measures);

        // Then
        assertThat(returnValue).isEqualTo(SonarQubeProject.builder()
                .key("test-project-key-1")
                .name("Test Project Name 1")
                .url(BASE_URL + "/dashboard?id=test-project-key-1")
                .measures(measures)
                .lastCommitTimestamp(LocalDateTime.of(2021, 1, 2, 3, 4, 5))
                .build());
    }
}