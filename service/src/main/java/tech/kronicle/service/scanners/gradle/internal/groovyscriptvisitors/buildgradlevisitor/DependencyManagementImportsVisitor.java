package tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import tech.kronicle.service.scanners.gradle.internal.services.BuildFileLoader;
import tech.kronicle.service.scanners.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.service.scanners.gradle.internal.services.DependencyVersionFetcher;
import tech.kronicle.service.scanners.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.service.scanners.gradle.internal.services.PropertyExpander;
import tech.kronicle.service.scanners.gradle.internal.services.PropertyRetriever;
import tech.kronicle.service.scanners.gradle.internal.services.SoftwareRepositoryFactory;
import tech.kronicle.service.scanners.gradle.internal.utils.ArtifactUtils;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DependencyManagementImportsVisitor extends BaseArtifactVisitor {

    private final DependencyVersionFetcher dependencyVersionFetcher;

    public DependencyManagementImportsVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator,
                                              PropertyExpander propertyExpander, PropertyRetriever propertyRetriever, ArtifactUtils artifactUtils,
                                              DependencyVersionFetcher dependencyVersionFetcher, SoftwareRepositoryFactory softwareRepositoryFactory) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, propertyExpander, propertyRetriever, artifactUtils, softwareRepositoryFactory);
        this.dependencyVersionFetcher = dependencyVersionFetcher;
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected boolean shouldProcessArguments(MethodCallExpression call) {
        return call.getMethodAsString().equals("mavenBom");
    }

    @Override
    protected void addArtifact(String groupId, String artifactId, String version, String packaging) {
        String artifact = artifactUtils().createArtifact(groupId, artifactId, version, packaging);
        dependencyVersionFetcher.findDependencyVersions(
                visitorState().getScannerId(),
                artifact,
                getSoftwareRepositories(),
                visitorState().getDependencyVersions(),
                visitorState().getSoftware());
    }
}
