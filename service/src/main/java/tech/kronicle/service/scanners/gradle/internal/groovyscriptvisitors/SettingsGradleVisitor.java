package tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors;

import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor.PluginsVisitor;
import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor.RepositoriesVisitor;
import tech.kronicle.service.scanners.gradle.internal.services.BuildFileLoader;
import tech.kronicle.service.scanners.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.service.scanners.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.service.scanners.gradle.internal.services.PluginProcessor;
import tech.kronicle.service.scanners.gradle.internal.services.SoftwareRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SettingsGradleVisitor extends BaseBuildFileVisitor {

    public SettingsGradleVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator,
                                 PluginsVisitor pluginsVisitor, RepositoriesVisitor repositoriesVisitor, SoftwareRepositoryFactory softwareRepositoryFactory,
                                 PluginProcessor pluginProcessor) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, pluginsVisitor, repositoriesVisitor, softwareRepositoryFactory, pluginProcessor);
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected ExpressionVisitOutcome processMethodCallExpression(MethodCallExpression call) {
        if (call.getMethodAsString().equals("pluginManagement")) {
            log.debug("Found pluginManagement");
            return ExpressionVisitOutcome.CONTINUE;
        }

        return super.processMethodCallExpression(call);
    }

    @Override
    protected ProcessPhase getRepositoriesProcessPhase() {
        return ProcessPhase.BUILDSCRIPT_REPOSITORIES;
    }

    @Override
    protected void processApplyPlugin(Map<String, String> values) {
    }
}
