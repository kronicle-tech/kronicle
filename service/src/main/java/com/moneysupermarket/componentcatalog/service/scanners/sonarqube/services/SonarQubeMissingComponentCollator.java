package com.moneysupermarket.componentcatalog.service.scanners.sonarqube.services;

import com.moneysupermarket.componentcatalog.sdk.models.SummaryMissingComponent;
import com.moneysupermarket.componentcatalog.service.scanners.sonarqube.models.Project;
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
