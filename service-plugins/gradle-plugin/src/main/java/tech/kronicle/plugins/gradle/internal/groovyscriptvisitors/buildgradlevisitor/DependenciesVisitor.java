package tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;
import tech.kronicle.common.StringEscapeUtils;
import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.ProcessPhase;
import tech.kronicle.plugins.gradle.internal.models.PomOutcome;
import tech.kronicle.plugins.gradle.internal.services.ArtifactVersionResolver;
import tech.kronicle.plugins.gradle.internal.services.BillOfMaterialsLogger;
import tech.kronicle.plugins.gradle.internal.services.BuildFileLoader;
import tech.kronicle.plugins.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.plugins.gradle.internal.services.DependencyVersionFetcher;
import tech.kronicle.plugins.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.plugins.gradle.internal.services.PomFetcher;
import tech.kronicle.plugins.gradle.internal.services.SoftwareRepositoryFactory;
import tech.kronicle.plugins.gradle.internal.utils.ArtifactUtils;
import tech.kronicle.plugins.gradle.internal.utils.InheritingHashSet;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareScope;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static tech.kronicle.sdk.models.SoftwareType.JVM;

@Slf4j
public class DependenciesVisitor extends BaseArtifactVisitor {

    private final ArtifactVersionResolver artifactVersionResolver;
    private final PomFetcher pomFetcher;
    private final PlatformVisitor platformVisitor;

    @Inject
    public DependenciesVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator,
                               SoftwareRepositoryFactory softwareRepositoryFactory, ArtifactUtils artifactUtils, DependencyVersionFetcher dependencyVersionFetcher,
                               BillOfMaterialsLogger billOfMaterialsLogger, PlatformVisitor platformVisitor, ArtifactVersionResolver artifactVersionResolver,
                               PomFetcher pomFetcher) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, softwareRepositoryFactory, artifactUtils, dependencyVersionFetcher, billOfMaterialsLogger);
        this.platformVisitor = platformVisitor;
        this.artifactVersionResolver = artifactVersionResolver;
        this.pomFetcher = pomFetcher;
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected void processPlatform(MethodCallExpression call) {
        log().debug("Found platform");
        visit(call, platformVisitor);
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
                throw new IllegalArgumentException("Version could not be found for artifact \"" + StringEscapeUtils.escapeString(name) + "\"");
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
