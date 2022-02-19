package tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import tech.kronicle.service.scanners.gradle.internal.services.BuildFileLoader;
import tech.kronicle.service.scanners.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.service.scanners.gradle.internal.services.BillOfMaterialsLogger;
import tech.kronicle.service.scanners.gradle.internal.services.DependencyVersionFetcher;
import tech.kronicle.service.scanners.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.service.scanners.gradle.internal.services.SoftwareRepositoryFactory;
import tech.kronicle.service.scanners.gradle.internal.utils.ArtifactUtils;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;

import javax.inject.Singleton;

@Singleton
@Slf4j
public class DependencyManagementImportsVisitor extends BaseArtifactVisitor {

    public DependencyManagementImportsVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator,
                                              SoftwareRepositoryFactory softwareRepositoryFactory, ArtifactUtils artifactUtils,
                                              DependencyVersionFetcher dependencyVersionFetcher, BillOfMaterialsLogger billOfMaterialsLogger) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, softwareRepositoryFactory, artifactUtils, dependencyVersionFetcher, billOfMaterialsLogger);
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
        addBillOfMaterialsArtifact(groupId, artifactId, version, packaging);
    }
}
