package tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.BaseVisitor;
import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.ExpressionVisitOutcome;
import tech.kronicle.plugins.gradle.internal.services.BuildFileLoader;
import tech.kronicle.plugins.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.plugins.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.plugins.gradle.internal.services.PluginProcessor;
import tech.kronicle.plugins.gradle.internal.services.SoftwareRepositoryFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@Slf4j
public class PluginsVisitor extends BaseVisitor {

    private final PluginProcessor pluginProcessor;

    public PluginsVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator,
                          PluginProcessor pluginProcessor, SoftwareRepositoryFactory softwareRepositoryFactory) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, softwareRepositoryFactory);
        this.pluginProcessor = pluginProcessor;
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected ExpressionVisitOutcome processMethodCallExpression(MethodCallExpression call) {
        if (call.getMethodAsString().equals("id")) {
            processPlugin(call);
            return ExpressionVisitOutcome.PROCESSED;
        } else if (call.getMethodAsString().equals("version")) {
            processPlugin(call);
            return ExpressionVisitOutcome.PROCESSED;
        } else if (call.getMethodAsString().equals("apply")) {
            processPlugin(call);
            return ExpressionVisitOutcome.PROCESSED;
        }

        return ExpressionVisitOutcome.IGNORED;
    }

    private void processPlugin(MethodCallExpression call) {
        Map<String, String> values = getValues(call);
        String name = values.get("id");
        String version = values.get("version");
        boolean apply = getApplyValue(values);

        pluginProcessor.processPlugin(visitorState().getScannerId(), name, version, apply, visitorState().getSoftware());
    }

    private boolean getApplyValue(Map<String, String> values) {
        String text = values.get("apply");
        if (isNull(text) || Objects.equals(text, "true")) {
            return true;
        } else if (Objects.equals(text, "false")) {
            return false;
        } else {
            throw new RuntimeException(String.format("Unexpected value \"%s\" for apply argument to plugin call", text));
        }
    }

    private Map<String, String> getValues(MethodCallExpression call) {
        Map<String, String> values = new HashMap<>();
        do {
            addValue(values, call);
            call = call.getObjectExpression() instanceof MethodCallExpression
                ? (MethodCallExpression) call.getObjectExpression()
                : null;
        } while (nonNull(call));
        return values;
    }

    private void addValue(Map<String, String> values, MethodCallExpression call) {
        String argumentValue = getArgumentValue(call);
        values.put(call.getMethodAsString(), argumentValue);
    }

    private String getArgumentValue(MethodCallExpression call) {
        ArgumentListExpression arguments = (ArgumentListExpression) call.getArguments();
        if (arguments.getExpressions().size() == 1) {
            return evaluateExpression(arguments.getExpression(0));
        } else {
            throw new RuntimeException(String.format("Method has %d arguments but only 1 argument is supported", arguments.getExpressions().size()));
        }
    }
}
