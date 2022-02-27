package tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;
import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.BaseVisitor;
import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.ExpressionVisitOutcome;
import tech.kronicle.plugins.gradle.internal.services.BuildFileLoader;
import tech.kronicle.plugins.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.plugins.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.plugins.gradle.internal.services.PropertyExpander;
import tech.kronicle.plugins.gradle.internal.services.SoftwareRepositoryFactory;

import javax.inject.Inject;

@Slf4j
public class ExtVisitor extends BaseVisitor {

    private final PropertyExpander propertyExpander;

    @Inject
    public ExtVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator,
                      SoftwareRepositoryFactory softwareRepositoryFactory, PropertyExpander propertyExpander) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, softwareRepositoryFactory);
        this.propertyExpander = propertyExpander;
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected ExpressionVisitOutcome processMethodCallExpression(MethodCallExpression call) {
        if (call.getMethodAsString().equals("set")) {
            log.debug("Found ext.set");
            if (call.getArguments() instanceof ArgumentListExpression) {
                ArgumentListExpression arguments = (ArgumentListExpression) call.getArguments();
                if (arguments.getExpressions().size() == 2) {
                    visitorState().getProperties().put(arguments.getExpression(0).getText(), arguments.getExpression(1).getText());
                }
            }
            return ExpressionVisitOutcome.PROCESSED;
        }

        return ExpressionVisitOutcome.IGNORED;
    }

    @Override
    protected ExpressionVisitOutcome processBinaryExpression(BinaryExpression expression) {
        String rightExpressionText = expression.getRightExpression().getText();
        if (!rightExpressionText.isEmpty()) {
            rightExpressionText = propertyExpander.expandProperties(rightExpressionText,
                    "expression",
                    visitorState().getProperties(),
                    false);
        }
        visitorState().getProperties().put(expression.getLeftExpression().getText(), rightExpressionText);
        return ExpressionVisitOutcome.PROCESSED;
    }
}
