package tech.kronicle.plugins.gradle.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import tech.kronicle.plugins.gradle.models.Configuration;
import tech.kronicle.plugins.gradle.models.GradleDependencies;
import tech.kronicle.plugins.gradle.models.ResolvedDependency;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.utils.FileUtils;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DependencyFileAnalyzer {

    private static final String GRADLE_DEPENDENCIES_YAML_FILE_NAME = "gradle-dependencies.yaml";

    private final FileUtils fileUtils;
    private final YAMLMapper yamlMapper;

    public List<Path> findDependencyFiles(Path dir) {
        return fileUtils.findFiles(dir, this::isDependencyFile)
                .collect(toUnmodifiableList());
    }

    private boolean isDependencyFile(Path path, BasicFileAttributes basicFileAttributes) {
        return Objects.equals(path.getFileName().toString(), GRADLE_DEPENDENCIES_YAML_FILE_NAME);
    }

    @SneakyThrows
    public List<Software> analyzeDependencyFile(Path dir) {
        return getSoftwaresFromGradleDependencies(readGradleDependenciesFile(dir));
    }

    private GradleDependencies readGradleDependenciesFile(Path dir) throws JsonProcessingException {
        return yamlMapper.readValue(fileUtils.readFileContent(dir), GradleDependencies.class);
    }

    private List<Software> getSoftwaresFromGradleDependencies(GradleDependencies gradleDependencies) {
        return gradleDependencies.getConfigurations().stream()
                .map(this::getSoftwaresFromConfiguration)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }

    private List<Software> getSoftwaresFromConfiguration(Configuration configuration) {
        return getSoftwaresFromResolvedDependencies(
                configuration.getResolvedDependencies(),
                SoftwareDependencyType.DIRECT
        );
    }

    private List<Software> getSoftwaresFromResolvedDependencies(
            List<ResolvedDependency> resolvedDependencies,
            SoftwareDependencyType dependencyType
    ) {
        if (isNull(resolvedDependencies)) {
            return List.of();
        }
        return resolvedDependencies.stream()
                .map(resolvedDependency -> getSoftwaresFromResolvedDependency(resolvedDependency, dependencyType))
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }

    private List<Software> getSoftwaresFromResolvedDependency(
            ResolvedDependency resolvedDependency,
            SoftwareDependencyType dependencyType
    ) {
        List<Software> softwares = new ArrayList<>();
        softwares.add(mapSoftware(resolvedDependency, dependencyType));
        softwares.addAll(getSoftwaresFromResolvedDependencies(
                resolvedDependency.getResolvedDependencies(),
                SoftwareDependencyType.TRANSITIVE
        ));
        return softwares;
    }

    private Software mapSoftware(ResolvedDependency resolvedDependency, SoftwareDependencyType dependencyType) {
        return Software.builder()
                .dependencyType(dependencyType)
                .name(resolvedDependency.getModuleGroup() + ":" + resolvedDependency.getModuleName())
                .version(resolvedDependency.getModuleVersion())
                .build();
    }
}
