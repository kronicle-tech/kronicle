package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.constants.SoftwareRepositoryUrls;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.BaseVisitor;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.ExpressionVisitOutcome;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.BuildFileLoader;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.BuildFileProcessor;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.ExpressionEvaluator;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.SoftwareRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RepositoriesVisitor extends BaseVisitor {

    private final MavenRepositoryVisitor mavenRepositoryVisitor;

    public RepositoriesVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator,
            MavenRepositoryVisitor mavenRepositoryVisitor, SoftwareRepositoryFactory softwareRepositoryFactory) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, softwareRepositoryFactory);
        this.mavenRepositoryVisitor = mavenRepositoryVisitor;
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected ExpressionVisitOutcome processMethodCallExpression(MethodCallExpression call) {
        if (call.getMethodAsString().equals("maven")) {
            visit(call.getArguments(), mavenRepositoryVisitor);
            return ExpressionVisitOutcome.PROCESSED;
        } if (call.getMethodAsString().equals("gradlePluginPortal")) {
            addSoftwareRepository(SoftwareRepositoryUrls.GRADLE_PLUGIN_PORTAL);
            return ExpressionVisitOutcome.PROCESSED;
        } if (call.getMethodAsString().equals("mavenCentral")) {
            addSoftwareRepository(SoftwareRepositoryUrls.MAVEN_CENTRAL);
            return ExpressionVisitOutcome.PROCESSED;
        } if (call.getMethodAsString().equals("jcenter")) {
            addSoftwareRepository(SoftwareRepositoryUrls.JCENTER);
            return ExpressionVisitOutcome.PROCESSED;
        } if (call.getMethodAsString().equals("google")) {
            addSoftwareRepository(SoftwareRepositoryUrls.GOOGLE);
            return ExpressionVisitOutcome.PROCESSED;
        }

        return ExpressionVisitOutcome.IGNORED;
    }
}
