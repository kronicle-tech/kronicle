package tech.kronicle.service.scanners.gradle.internal.services;

import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareType;
import tech.kronicle.service.scanners.gradle.internal.utils.InheritingHashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.service.scanners.gradle.internal.constants.GradlePlugins;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class PluginProcessor {

    private static final List<SoftwareType> PLUGIN_SOFTWARE_TYPES = List.of(SoftwareType.GRADLE_PLUGIN, SoftwareType.GRADLE_PLUGIN_VERSION);

    public void processPlugin(String scannerId, String name, String version, boolean apply, Set<Software> software) {
        requireNonNull(name, "name");

        software.add(Software.builder()
                .scannerId(scannerId)
                .type(apply ? SoftwareType.GRADLE_PLUGIN : SoftwareType.GRADLE_PLUGIN_VERSION)
                .dependencyType(SoftwareDependencyType.DIRECT)
                .name(name)
                .version(getPluginVersion(name, version, software))
                .build());
    }

    private String getPluginVersion(String name, String version, Set<Software> software) {
        if (nonNull(version)) {
            return version;
        }

        return getPlugin(name, software)
                .map(Software::getVersion)
                .orElse(null);
    }

    public Optional<Software> getPlugin(String name, Set<Software> software) {
        return software.stream()
                .filter(item -> nonNull(item.getType()) && PLUGIN_SOFTWARE_TYPES.contains(item.getType()) && Objects.equals(item.getName(), name))
                .findFirst();
    }

    public int getPluginCount(Set<Software> software) {
        return (int) software.stream()
                .filter(element -> Objects.equals(element.getType(), SoftwareType.GRADLE_PLUGIN))
                .count();
    }

    public Optional<Software> getMicronautApplicationPlugin(InheritingHashSet<Software> software) {
        return software.stream()
                .filter(item -> Objects.equals(item.getType(), SoftwareType.GRADLE_PLUGIN)
                        && Objects.equals(item.getName(), GradlePlugins.MICRONAUT_APPLICATION)
                        && nonNull(item.getVersion()))
                .findFirst();
    }

    public Optional<Software> getSpringBootPlugin(Set<Software> software) {
        return software.stream()
                .filter(item -> Objects.equals(item.getType(), SoftwareType.GRADLE_PLUGIN)
                        && Objects.equals(item.getName(), GradlePlugins.SPRING_BOOT)
                        && nonNull(item.getVersion()))
                .findFirst();
    }

    public Optional<Software> getSpringBootPluginDependency(InheritingHashSet<Software> software) {
        return software.stream()
                .filter(item -> Objects.equals(item.getName(), "org.springframework.boot:spring-boot-gradle-plugin"))
                .findFirst();
    }
}
