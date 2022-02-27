package tech.kronicle.plugins.sonarqube.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.sonarqube.client.SonarQubeClient;
import tech.kronicle.plugins.sonarqube.config.SonarQubeConfig;
import tech.kronicle.plugins.sonarqube.models.Project;
import tech.kronicle.sdk.models.SummaryMissingComponent;
import tech.kronicle.sdk.models.sonarqube.SonarQubeMeasure;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;
import tech.kronicle.sdk.models.sonarqube.SummarySonarQubeMetric;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
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
