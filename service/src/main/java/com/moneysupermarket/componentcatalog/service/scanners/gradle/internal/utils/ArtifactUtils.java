package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.utils;

import lombok.Value;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

@Component
public class ArtifactUtils {

    private static final String ARTIFACT_DELIMITER = ":";

    public String createName(String groupId, String artifactId) {
        return groupId + ARTIFACT_DELIMITER + artifactId;
    }

    public String createArtifact(String groupId, String artifactId, String version) {
        return createName(groupId, artifactId) + ARTIFACT_DELIMITER + version;
    }

    public String createArtifact(String groupId, String artifactId, String version, String packaging) {
        String artifact = createArtifact(groupId, artifactId, version);

        if (nonNull(packaging)) {
            artifact += ARTIFACT_DELIMITER + packaging;
        }

        return artifact;
    }

    public String createArtifactFromNameAndVersion(String name, String version) {
        return name + ARTIFACT_DELIMITER + version;
    }

    public ArtifactParts getArtifactParts(String artifact) {
        requireNonNull(artifact, "artifact");
        String[] parts = artifact.split(":");
        if (parts.length < 3) {
            throw new IllegalArgumentException("artifact must contain at least 3 parts");
        }
        String packaging = (parts.length > 3) ? parts[3] : null;
        return new ArtifactParts(parts[0], parts[1], createName(parts[0], parts[1]), parts[2], packaging);
    }

    @Value
    public static class ArtifactParts {

        String groupId;
        String artifactId;
        String name;
        String version;
        String packaging;
    }

}
