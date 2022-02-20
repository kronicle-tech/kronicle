package tech.kronicle.plugins.gradle.internal.models.mavenxml;

public interface ProjectCoordinates {

    String getGroupId();
    String getArtifactId();
    String getVersion();
    String getPackaging();
}
