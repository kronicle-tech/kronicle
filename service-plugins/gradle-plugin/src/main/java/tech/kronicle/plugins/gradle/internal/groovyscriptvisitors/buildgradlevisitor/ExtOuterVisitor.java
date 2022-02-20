package tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.BaseVisitor;
import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.ExpressionVisitOutcome;
import tech.kronicle.plugins.gradle.internal.services.BuildFileLoader;
import tech.kronicle.plugins.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.plugins.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.plugins.gradle.internal.services.SoftwareRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.slf4j.Logger;
import tech.kronicle.service.spring.stereotypes.SpringComponent;

import java.util.Objects;

@SpringComponent
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
