package tech.kronicle.plugins.gradle.models;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Configuration {

    String name;
    String description;
    Boolean visible;
    Boolean canBeResolved;
    Boolean resolved;
    List<Dependency> dependencies;
    List<ResolvedDependency> resolvedDependencies;
}
