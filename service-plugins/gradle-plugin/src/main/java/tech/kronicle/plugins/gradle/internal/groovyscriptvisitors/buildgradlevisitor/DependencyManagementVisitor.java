package tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;
import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.BaseVisitor;
import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.ExpressionVisitOutcome;
import tech.kronicle.plugins.gradle.internal.services.BuildFileLoader;
import tech.kronicle.plugins.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.plugins.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.plugins.gradle.internal.services.SoftwareRepositoryFactory;

import javax.inject.Inject;

@Slf4j
public class DependencyManagementVisitor extends BaseVisitor {

    private final DependencyManagementImportsVisitor dependencyManagementImportsVisitor;

    @Inject
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
