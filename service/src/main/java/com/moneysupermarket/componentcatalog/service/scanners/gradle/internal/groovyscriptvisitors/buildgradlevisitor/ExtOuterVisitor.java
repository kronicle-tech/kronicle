package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.BaseVisitor;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.ExpressionVisitOutcome;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.BuildFileLoader;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.BuildFileProcessor;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.ExpressionEvaluator;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.SoftwareRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class ExtOuterVisitor extends BaseVisitor {

    private final ExtVisitor extVisitor;

    public ExtOuterVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator,
            ExtVisitor extVisitor, SoftwareRepositoryFactory softwareRepositoryFactory) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, softwareRepositoryFactory);
        this.extVisitor = extVisitor;
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected ExpressionVisitOutcome processMethodCallExpression(MethodCallExpression call) {
        Expression objectExpression = call.getObjectExpression();

        if (objectExpression instanceof VariableExpression) {
            String objectName = ((VariableExpression) objectExpression).getName();

            if (Objects.equals(objectName, "this") || Objects.equals(objectName, "project")) {
                log.debug("Found {}.ext", objectName);
                visit(call.getArguments(), extVisitor);
                return ExpressionVisitOutcome.PROCESSED;
            }
        }

        return ExpressionVisitOutcome.IGNORED;
    }
}
