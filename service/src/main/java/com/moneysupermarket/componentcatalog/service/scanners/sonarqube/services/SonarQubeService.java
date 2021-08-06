package com.moneysupermarket.componentcatalog.service.scanners.sonarqube.services;

import com.moneysupermarket.componentcatalog.sdk.models.SummaryMissingComponent;
import com.moneysupermarket.componentcatalog.sdk.models.sonarqube.SonarQubeMeasure;
import com.moneysupermarket.componentcatalog.sdk.models.sonarqube.SonarQubeProject;
import com.moneysupermarket.componentcatalog.sdk.models.sonarqube.SummarySonarQubeMetric;
import com.moneysupermarket.componentcatalog.service.scanners.sonarqube.client.SonarQubeClient;
import com.moneysupermarket.componentcatalog.service.scanners.sonarqube.models.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class SonarQubeService {

    private final CodebaseSonarQubeProjectFinder projectFinder;
    private final SonarQubeClient client;
    private final SonarQubeProjectCache projectCache;
    private final SonarQubeProjectCreator projectCreator;
    private final SonarQubeMissingComponentCollator missingComponentCollator;
    private List<SummarySonarQubeMetric> metrics;
    private List<Project> projects;

    public void refresh() {
        projectCache.clear();
        metrics = client.getMetrics();
        projects = client.getProjects();
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
}
