package tech.kronicle.plugins.gradle.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.gradlestaticanalyzer.GradleAnalysis;
import tech.kronicle.gradlestaticanalyzer.GradleStaticAnalyzer;
import tech.kronicle.plugins.gradle.GradlePlugin;
import tech.kronicle.plugins.gradle.GradleScanner;
import tech.kronicle.sdk.models.*;
import tech.kronicle.sdk.models.gradle.GradleState;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class GradleDependenciesFinder {

    private final DependencyFileAnalyzer dependencyFileAnalyzer;
    private final GradleStaticAnalyzer gradleStaticAnalyzer;

    public List<ComponentState> findDependencies(Path dir) {
        GradleAnalysis gradleAnalysis = analyzeCodebase(dir);

        return createStates(gradleAnalysis);
    }

    private GradleAnalysis analyzeCodebase(Path dir) {
        List<Path> dependencyFiles = dependencyFileAnalyzer.findDependencyFiles(dir);

        if (!dependencyFiles.isEmpty()) {
            return new GradleAnalysis(
                    true,
                    List.of(),
                    analyzeDependencyFiles(dependencyFiles)
            );
        } else {
            return analyzeBuildScripts(dir);
        }
    }

    private List<Software> analyzeDependencyFiles(List<Path> dependencyFiles) {
        return dependencyFiles.stream()
                .map(dependencyFileAnalyzer::analyzeDependencyFile)
                .flatMap(Collection::stream)
                .distinct()
                .collect(toUnmodifiableList());
    }

    private GradleAnalysis analyzeBuildScripts(Path dir) {
        return gradleStaticAnalyzer.analyzeCodebase(dir);
    }

    private List<SoftwareRepository> setScannerIdOnSoftwareRepositories(List<SoftwareRepository> softwareRepositories) {
        return softwareRepositories.stream()
                .map(it -> it.withScannerId(GradleScanner.ID))
                .collect(Collectors.toList());
    }

    private List<Software> setScannerIdOnSoftware(List<Software> software) {
        return software.stream()
                .map(it -> it.withScannerId(GradleScanner.ID))
                .collect(Collectors.toList());
    }

    private List<ComponentState> createStates(GradleAnalysis gradleAnalysis) {
        List<ComponentState> states = new ArrayList<>();
        states.add(new GradleState(GradlePlugin.ID, gradleAnalysis.getGradleIsUsed()));

        if (!gradleAnalysis.getSoftwareRepositories().isEmpty()) {
            states.add(new SoftwareRepositoriesState(
                    GradlePlugin.ID,
                    setScannerIdOnSoftwareRepositories(gradleAnalysis.getSoftwareRepositories())
            ));
        }

        if (!gradleAnalysis.getSoftware().isEmpty()) {
            states.add(new SoftwaresState(
                    GradlePlugin.ID,
                    setScannerIdOnSoftware(gradleAnalysis.getSoftware())
            ));
        }
        return states;
    }
}
