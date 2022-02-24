package tech.kronicle.plugins.sonarqube.services;

import tech.kronicle.plugins.sonarqube.constants.MetricKeys;
import tech.kronicle.plugins.sonarqube.constants.WebPaths;
import tech.kronicle.sdk.models.sonarqube.SonarQubeMeasure;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.plugins.sonarqube.config.SonarQubeConfig;
import tech.kronicle.plugins.sonarqube.models.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SonarQubeProjectCreator {

    private final SonarQubeConfig config;

    public SonarQubeProject create(Project project, List<SonarQubeMeasure> measures) {
        String projectKey = project.getKey();
        return new SonarQubeProject(projectKey, project.getName(), createProjectUrl(projectKey), getLastCommitTimestamp(measures), measures);
    }

    private LocalDateTime getLastCommitTimestamp(List<SonarQubeMeasure> measures) {
        return measures.stream()
                .filter(measure -> Objects.equals(measure.getMetric(), MetricKeys.LAST_COMMIT_DATE))
                .findFirst()
                .map(SonarQubeMeasure::getValue)
                .map(Long::parseLong)
                .map(lastCommitDate -> LocalDateTime.ofEpochSecond(lastCommitDate, 0, ZoneOffset.UTC))
                .orElse(null);
    }

    private String createProjectUrl(String projectKey) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(config.getBaseUrl());
        builder.path(WebPaths.DASHBOARD_PATH).queryParam("id", projectKey);
        return builder.build().toUriString();
    }
}
