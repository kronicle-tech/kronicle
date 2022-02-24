package tech.kronicle.plugins.sonarqube.services;

import tech.kronicle.sdk.models.SummaryMissingComponent;
import tech.kronicle.plugins.sonarqube.models.Project;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SonarQubeMissingComponentCollator {

    public List<SummaryMissingComponent> getMissingComponents(String scannerId, List<Project> projects, Set<String> usedProjectKeys) {
        return getMissingProjectKeys(projects, usedProjectKeys).stream()
                .map(missingProjectKey -> createMissingComponent(missingProjectKey, scannerId))
                .collect(Collectors.toList());
    }

    private SummaryMissingComponent createMissingComponent(String missingProjectKey, String scannerId) {
        return SummaryMissingComponent.builder()
                .id(missingProjectKey)
                .scannerId(scannerId)
                .build();
    }

    private List<String> getMissingProjectKeys(List<Project> projects, Set<String> usedProjectKeys) {
        return projects.stream()
                .map(Project::getKey)
                .filter(projectKey -> !usedProjectKeys.contains(projectKey))
                .sorted()
                .collect(Collectors.toList());
    }
}
