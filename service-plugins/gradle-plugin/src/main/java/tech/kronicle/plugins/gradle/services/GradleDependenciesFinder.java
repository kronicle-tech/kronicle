package tech.kronicle.plugins.gradle.services;

import lombok.RequiredArgsConstructor;
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

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class GradleDependenciesFinder {

    private final DependencyFileAnalyzer dependencyFileAnalyzer;
    private final GradleWrapperFinder gradleWrapperFinder;

    public List<ComponentState> findDependencies(Path dir) {
        List<Software> softwares = analyzeCodebase(dir);
        return createStates(softwares);
    }

    private List<Software> analyzeCodebase(Path dir) {
        List<Path> dependencyFiles = dependencyFileAnalyzer.findDependencyFiles(dir);

        List<Software> softwares = new ArrayList<>();
        Software gradleWrapper = gradleWrapperFinder.findGradleWrapper(dir);
        if (nonNull(gradleWrapper)) {
            softwares.add(gradleWrapper);
        }
        softwares.addAll(analyzeDependencyFiles(dependencyFiles));
        return softwares;
    }

    private List<Software> analyzeDependencyFiles(List<Path> dependencyFiles) {
        return dependencyFiles.stream()
                .map(dependencyFileAnalyzer::analyzeDependencyFile)
                .flatMap(Collection::stream)
                .distinct()
                .collect(toUnmodifiableList());
    }

    private List<Software> setScannerIdOnSoftware(List<Software> software) {
        return software.stream()
                .map(it -> it.withScannerId(GradleScanner.ID))
                .collect(Collectors.toList());
    }

    private List<ComponentState> createStates(List<Software> softwares) {
        List<ComponentState> states = new ArrayList<>();
        states.add(new GradleState(GradlePlugin.ID, !softwares.isEmpty()));

        if (!softwares.isEmpty()) {
            states.add(new SoftwaresState(
                    GradlePlugin.ID,
                    setScannerIdOnSoftware(softwares)
            ));
        }
        return states;
    }
}
