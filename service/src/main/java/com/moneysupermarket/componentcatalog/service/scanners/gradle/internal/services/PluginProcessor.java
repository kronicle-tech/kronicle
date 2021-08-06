package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services;

import com.moneysupermarket.componentcatalog.sdk.models.Software;
import com.moneysupermarket.componentcatalog.sdk.models.SoftwareDependencyType;
import com.moneysupermarket.componentcatalog.sdk.models.SoftwareType;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.utils.InheritingHashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.constants.GradlePlugins.SPRING_BOOT;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class PluginProcessor {

    public void processPlugin(String scannerId, String name, String version, Set<Software> software) {
        requireNonNull(name, "name");

        software.add(Software.builder()
                .scannerId(scannerId)
                .type(SoftwareType.GRADLE_PLUGIN)
                .dependencyType(SoftwareDependencyType.DIRECT)
                .name(name)
                .version(version)
                .build());
    }

    public int getPluginCount(Set<Software> software) {
        return (int) software.stream()
                .filter(element -> Objects.equals(element.getType(), SoftwareType.GRADLE_PLUGIN))
                .count();
    }

    public Optional<Software> getSpringBootPlugin(InheritingHashSet<Software> software) {
        return software.stream()
                .filter(item -> Objects.equals(item.getType(), SoftwareType.GRADLE_PLUGIN)
                        && Objects.equals(item.getName(), SPRING_BOOT)
                        && nonNull(item.getVersion()))
                .findFirst();
    }

    public Optional<Software> getSpringBootPluginDependency(InheritingHashSet<Software> software) {
        return software.stream()
                .filter(item -> Objects.equals(item.getName(), "org.springframework.boot:spring-boot-gradle-plugin"))
                .findFirst();
    }
}
