package tech.kronicle.plugins.gradle.internal.services;

import org.springframework.stereotype.Component;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareType;
import tech.kronicle.plugins.gradle.internal.utils.InheritingHashSet;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class PluginProcessor {

    private static final List<SoftwareType> PLUGIN_AND_PLUGIN_VERSION = List.of(SoftwareType.GRADLE_PLUGIN, SoftwareType.GRADLE_PLUGIN_VERSION);
    private static final List<SoftwareType> PLUGIN = List.of(SoftwareType.GRADLE_PLUGIN);

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

        return getPluginVersion(name, software).orElse(null);
    }

    public Optional<Software> getPlugin(String name, Set<Software> software) {
        return getSoftware(name, PLUGIN, software);
    }

    public Optional<String> getPluginVersion(String name, Set<Software> software) {
        return getSoftware(name, PLUGIN_AND_PLUGIN_VERSION, software)
                .map(Software::getVersion);
    }

    public Optional<Software> getSoftware(String name, List<SoftwareType> softwareTypes, Set<Software> software) {
        return software.stream()
                .filter(item -> nonNull(item.getType())
                        && softwareTypes.contains(item.getType())
                        && Objects.equals(item.getName(), name))
                .findFirst();
    }

    public int getPluginCount(Set<Software> software) {
        return (int) software.stream()
                .filter(element -> Objects.equals(element.getType(), SoftwareType.GRADLE_PLUGIN))
                .count();
    }

    public Optional<Software> getSpringBootPluginDependency(InheritingHashSet<Software> software) {
        return software.stream()
                .filter(item -> Objects.equals(item.getName(), "org.springframework.boot:spring-boot-gradle-plugin"))
                .findFirst();
    }
}
