package tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;
import tech.kronicle.service.scanners.gradle.internal.constants.GradlePropertyNames;
import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.BaseBuildFileVisitor;
import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.ExpressionVisitOutcome;
import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.ProcessPhase;
import tech.kronicle.service.scanners.gradle.internal.services.BuildFileLoader;
import tech.kronicle.service.scanners.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.service.scanners.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.service.scanners.gradle.internal.services.PluginProcessor;
import tech.kronicle.service.scanners.gradle.internal.services.SoftwareRepositoryFactory;
import tech.kronicle.service.spring.stereotypes.SpringComponent;

@SpringComponent
@Slf4j
public class MicronautVisitor extends BaseBuildFileVisitor {

    public MicronautVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator, PluginsVisitor pluginsVisitor, RepositoriesVisitor repositoriesVisitor, SoftwareRepositoryFactory softwareRepositoryFactory, PluginProcessor pluginProcessor) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, pluginsVisitor, repositoriesVisitor, softwareRepositoryFactory, pluginProcessor);
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected ExpressionVisitOutcome processMethodCallExpression(MethodCallExpression call) {
        if (call.getMethodAsString().equals("version")) {
            log.debug("Found micronaut version");
            if (call.getArguments() instanceof ArgumentListExpression) {
                ArgumentListExpression arguments = (ArgumentListExpression) call.getArguments();
                if (arguments.getExpressions().size() == 1) {
                    visitorState().getProperties().put(GradlePropertyNames.MICRONAUT_VERSION, arguments.getExpression(0).getText());
                    return ExpressionVisitOutcome.PROCESSED;
                }
            }
            throw new RuntimeException("Unexpected format of version in micronaut block");
        }

        return ExpressionVisitOutcome.IGNORED;
    }
}
