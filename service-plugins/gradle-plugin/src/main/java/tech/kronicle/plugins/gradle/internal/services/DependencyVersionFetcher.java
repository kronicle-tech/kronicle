package tech.kronicle.plugins.gradle.internal.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.gradle.internal.constants.MavenPackagings;
import tech.kronicle.plugins.gradle.internal.models.Pom;
import tech.kronicle.plugins.gradle.internal.models.PomOutcome;
import tech.kronicle.plugins.gradle.internal.utils.ArtifactUtils;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareRepository;
import tech.kronicle.sdk.models.SoftwareType;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DependencyVersionFetcher {

    private final PomFetcher pomFetcher;
    private final ArtifactUtils artifactUtils;

    public void findDependencyVersions(String scannerId, String pomArtifactCoordinates, Set<SoftwareRepository> softwareRepositories,
            Map<String, Set<String>> dependencyVersions, Set<Software> software) {
        addProjectObjectModelSoftware(scannerId, pomArtifactCoordinates, SoftwareDependencyType.DIRECT, software);
        PomOutcome pomOutcome = pomFetcher.fetchPom(pomArtifactCoordinates, softwareRepositories);
        if (!pomOutcome.isJarOnly()) {
            Pom pom = pomOutcome.getPom();
            pom.getTransitiveArtifactCoordinates().forEach(artifact -> addProjectObjectModelSoftware(scannerId, artifact, SoftwareDependencyType.TRANSITIVE, software));
            if (nonNull(pom.getDependencyManagementDependencies())) {
                pom.getDependencyManagementDependencies().forEach(item -> {
                    Set<String> versions = dependencyVersions.get(item.getName());

                    if (isNull(versions)) {
                        versions = new HashSet<>();
                        dependencyVersions.put(item.getName(), versions);
                    }

                    versions.add(item.getVersion());
                });
            }
        }
    }

    private void addProjectObjectModelSoftware(String scannerId, String pomArtifactCoordinates, SoftwareDependencyType dependencyType,
            Set<Software> software) {
        ArtifactUtils.ArtifactParts parts = artifactUtils.getArtifactParts(pomArtifactCoordinates);
        software.add(new Software(scannerId, SoftwareType.JVM, dependencyType, parts.getName(), parts.getVersion(), null, MavenPackagings.BOM, null));
    }
}
