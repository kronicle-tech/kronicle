package tech.kronicle.plugins.gradle.internal.models;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import tech.kronicle.sdk.models.Software;

import java.util.Map;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class Pom {

    String artifactCoordinates;
    @Singular
    Map<String, String> properties;
    @Singular(value = "transitiveArtifactCoordinates")
    Set<String> transitiveArtifactCoordinates;
    @Singular
    Set<Software> dependencyManagementDependencies;
    @Singular
    Set<Software> dependencies;
}
