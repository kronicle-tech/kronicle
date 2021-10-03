package tech.kronicle.service.scanners.sonarqube.services;

import tech.kronicle.sdk.models.SummaryMissingComponent;
import tech.kronicle.sdk.models.sonarqube.SonarQubeMeasure;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.sdk.models.sonarqube.SummarySonarQubeMetric;
import tech.kronicle.service.scanners.sonarqube.client.SonarQubeClient;
import tech.kronicle.service.scanners.sonarqube.config.SonarQubeConfig;
import tech.kronicle.service.scanners.sonarqube.models.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class SonarQubeService {

    private final SonarQubeConfig config;
    private final SonarQubeClient client;
    private final CodebaseSonarQubeProjectFinder projectFinder;
    private final SonarQubeProjectCache projectCache;
    private final SonarQubeProjectCreator projectCreator;
    private final SonarQubeMissingComponentCollator missingComponentCollator;
    private List<SummarySonarQubeMetric> metrics;
    private List<Project> projects;

    public void refresh() {
        projectCache.clear();
        metrics = client.getMetrics();
        projects = getProjects();
    }

    public List<SummarySonarQubeMetric> getMetrics() {
        return metrics;
    }

    public List<SonarQubeProject> findProjects(Path codebaseDir) {
        List<SonarQubeProject> codebaseProjects = projectCache.get(codebaseDir);

        if (nonNull(codebaseProjects)) {
            return codebaseProjects;
        }

        codebaseProjects = projectFinder.findProjects(codebaseDir, projects).stream()
                .map(project -> {
                    List<SonarQubeMeasure> measures = client.getProjectMeasures(project.getKey(), metrics);
                    return projectCreator.create(project, measures);
                })
                .collect(Collectors.toList());
        projectCache.put(codebaseDir, codebaseProjects);
        return codebaseProjects;
    }

    public Collection<SummaryMissingComponent> getMissingComponents(String scannerId) {
        return missingComponentCollator.getMissingComponents(scannerId, projects, projectCache.getUsedProjectKeys());
    }

    private List<Project> getProjects() {
        List<String> organizations = config.getOrganizations();

        if (isNull(organizations) || organizations.isEmpty()) {
            return client.getProjects(null);
        }

        return organizations.stream()
                .map(client::getProjects)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
