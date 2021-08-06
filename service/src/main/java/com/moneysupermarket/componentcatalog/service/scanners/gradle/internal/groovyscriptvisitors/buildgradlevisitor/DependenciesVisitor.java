package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import com.moneysupermarket.componentcatalog.sdk.models.Software;
import com.moneysupermarket.componentcatalog.sdk.models.SoftwareDependencyType;
import com.moneysupermarket.componentcatalog.sdk.models.SoftwareScope;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.ProcessPhase;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.PomOutcome;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.ArtifactVersionResolver;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.BuildFileLoader;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.BuildFileProcessor;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.ExpressionEvaluator;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.PomFetcher;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.PropertyExpander;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.PropertyRetriever;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.SoftwareRepositoryFactory;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.utils.ArtifactUtils;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.utils.InheritingHashSet;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

import static com.moneysupermarket.componentcatalog.common.utils.StringEscapeUtils.escapeString;
import static com.moneysupermarket.componentcatalog.sdk.models.SoftwareType.JVM;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@Slf4j
public class DependenciesVisitor extends BaseArtifactVisitor {

    private final ArtifactVersionResolver artifactVersionResolver;
    private final PomFetcher pomFetcher;

    public DependenciesVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator, PropertyExpander propertyExpander, PropertyRetriever propertyRetriever,
            ArtifactUtils artifactUtils, ArtifactVersionResolver artifactVersionResolver, PomFetcher pomFetcher,
            SoftwareRepositoryFactory softwareRepositoryFactory) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, propertyExpander, propertyRetriever, artifactUtils, softwareRepositoryFactory);
        this.artifactVersionResolver = artifactVersionResolver;
        this.pomFetcher = pomFetcher;
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected void addArtifact(String groupId, String artifactId, String version, String packaging) {
        String name = artifactUtils().createName(groupId, artifactId);
        String versionSelector;
        Set<String> versions;

        if (nonNull(version)) {
            String newVersion = artifactVersionResolver.resolveArtifactVersion(groupId, artifactId, version, getSoftwareRepositories());
            versions = Set.of(newVersion);
            versionSelector = (!Objects.equals(version, newVersion)) ? version : null;
        } else {
            versions = visitorState().getDependencyVersions().get(name);
            if (isNull(versions) || versions.isEmpty()) {
                throw new IllegalArgumentException("Version could not be found for artifact \"" + escapeString(name) + "\"");
            }
            versionSelector = null;
        }

        SoftwareScope scope = (visitorState().getProcessPhase() == ProcessPhase.BUILDSCRIPT_DEPENDENCIES) ? SoftwareScope.BUILDSCRIPT : null;
        InheritingHashSet<Software> software = visitorState().getSoftware();

        versions.forEach(newVersion -> {
            software.add(new Software(visitorState().getScannerId(), JVM, SoftwareDependencyType.DIRECT, name, newVersion, versionSelector, packaging,
                    scope));

            PomOutcome pomOutcome = pomFetcher.fetchPom(
                    artifactUtils().createArtifact(groupId, artifactId, newVersion),
                    getSoftwareRepositories());
            if (!pomOutcome.isJarOnly() && nonNull(pomOutcome.getPom().getDependencies())) {
                pomOutcome.getPom().getDependencies().forEach(item -> software.add(item.withScannerId(visitorState().getScannerId())));
            }
        });
    }
}
