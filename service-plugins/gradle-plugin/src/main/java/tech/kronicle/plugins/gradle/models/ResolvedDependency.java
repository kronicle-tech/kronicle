package tech.kronicle.plugins.gradle.models;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ResolvedDependency {

    String name;
    String moduleName;
    String moduleGroup;
    String moduleVersion;
    List<ResolvedDependency> resolvedDependencies;
}
