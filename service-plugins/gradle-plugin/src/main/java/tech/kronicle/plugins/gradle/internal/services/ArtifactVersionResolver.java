package tech.kronicle.plugins.gradle.internal.services;

import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.CachingVersionSelectorScheme;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.DefaultVersionComparator;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.DefaultVersionSelectorScheme;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.Version;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionParser;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionSelector;
import tech.kronicle.common.StringEscapeUtils;
import tech.kronicle.plugins.gradle.internal.utils.ArtifactUtils;
import tech.kronicle.sdk.models.SoftwareRepository;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

public class ArtifactVersionResolver {

    private final ArtifactVersionsFetcher artifactVersionsFetcher;
    private final ArtifactUtils artifactUtils;
    private final CachingVersionSelectorScheme versionSelectorScheme;
    private final VersionParser versionParser;
    private final Comparator<Version> versionComparator;

    @Inject
    public ArtifactVersionResolver(ArtifactVersionsFetcher artifactVersionsFetcher, ArtifactUtils artifactUtils) {
        this.artifactVersionsFetcher = artifactVersionsFetcher;
        this.artifactUtils = artifactUtils;
        versionSelectorScheme = new CachingVersionSelectorScheme(
                new DefaultVersionSelectorScheme(new DefaultVersionComparator(), new VersionParser())
        );
        versionParser = new VersionParser();
        versionComparator = new DefaultVersionComparator().asVersionComparator().reversed();
    }

    public String resolveArtifactVersion(String groupId, String artifactId, String version, Set<SoftwareRepository> softwareRepositories) {
        VersionSelector versionSelector = versionSelectorScheme.parseSelector(version);

        if (!versionSelector.isDynamic()) {
            return version;
        }

        Optional<Version> highestMatchingVersion = artifactVersionsFetcher.fetchArtifactVersions(groupId, artifactId, softwareRepositories).stream()
                .map(versionParser::transform)
                .sorted(versionComparator)
                .filter(versionSelector::accept)
                .findFirst();

        if (highestMatchingVersion.isEmpty()) {
            throw new RuntimeException(String.format("Could not find matching version for \"%s\"",
                    StringEscapeUtils.escapeString(artifactUtils.createArtifact(groupId, artifactId, version))));
        }

        return highestMatchingVersion.get().getSource();
    }
}
