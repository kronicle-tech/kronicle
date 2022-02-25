package tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import tech.kronicle.plugins.gradle.internal.services.BillOfMaterialsLogger;
import tech.kronicle.plugins.gradle.internal.services.BuildFileLoader;
import tech.kronicle.plugins.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.plugins.gradle.internal.services.DependencyVersionFetcher;
import tech.kronicle.plugins.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.plugins.gradle.internal.services.SoftwareRepositoryFactory;
import tech.kronicle.plugins.gradle.internal.utils.ArtifactUtils;

@Component
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
