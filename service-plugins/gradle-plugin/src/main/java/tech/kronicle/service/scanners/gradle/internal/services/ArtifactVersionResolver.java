package tech.kronicle.service.scanners.gradle.internal.services;

import tech.kronicle.sdk.models.SoftwareRepository;
import tech.kronicle.service.scanners.gradle.internal.utils.ArtifactUtils;
import lombok.RequiredArgsConstructor;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.CachingVersionSelectorScheme;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.DefaultVersionComparator;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.DefaultVersionSelectorScheme;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.Version;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionParser;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionSelector;
import tech.kronicle.common.utils.StringEscapeUtils;
import tech.kronicle.service.spring.stereotypes.SpringComponent;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

@SpringComponent
@RequiredArgsConstructor
public class ArtifactVersionResolver {

    private final ArtifactVersionsFetcher artifactVersionsFetcher;
    private final ArtifactUtils artifactUtils;
    private CachingVersionSelectorScheme versionSelectorScheme;
    private VersionParser versionParser;
    private Comparator<Version> versionComparator;

    @PostConstruct
    public void initialize() {
        DefaultVersionSelectorScheme defaultVersionSelectorScheme = new DefaultVersionSelectorScheme(new DefaultVersionComparator(), new VersionParser());
        versionSelectorScheme = new CachingVersionSelectorScheme(defaultVersionSelectorScheme);
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
