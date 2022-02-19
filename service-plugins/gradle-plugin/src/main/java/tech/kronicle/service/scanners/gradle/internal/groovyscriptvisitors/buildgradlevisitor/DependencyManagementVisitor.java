package tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.BaseVisitor;
import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.ExpressionVisitOutcome;
import tech.kronicle.service.scanners.gradle.internal.services.BuildFileLoader;
import tech.kronicle.service.scanners.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.service.scanners.gradle.internal.services.BillOfMaterialsLogger;
import tech.kronicle.service.scanners.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.service.scanners.gradle.internal.services.SoftwareRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;

import javax.inject.Singleton;

@Singleton
@Slf4j
public class DependencyManagementVisitor extends BaseVisitor {

    private final DependencyManagementImportsVisitor dependencyManagementImportsVisitor;

    public DependencyManagementVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator,
                                       SoftwareRepositoryFactory softwareRepositoryFactory, DependencyManagementImportsVisitor dependencyManagementImportsVisitor) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, softwareRepositoryFactory);
        this.dependencyManagementImportsVisitor = dependencyManagementImportsVisitor;
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected ExpressionVisitOutcome processMethodCallExpression(MethodCallExpression call) {
        if (call.getMethodAsString().equals("imports")) {
            log.debug("Found imports");
            visit(call.getArguments(), dependencyManagementImportsVisitor);
            return ExpressionVisitOutcome.PROCESSED;
        }

        return ExpressionVisitOutcome.IGNORED;
    }
}
