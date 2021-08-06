package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.BaseBuildFileVisitor;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.ExpressionVisitOutcome;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors.ProcessPhase;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.BuildFileLoader;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.BuildFileProcessor;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.ExpressionEvaluator;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.PluginProcessor;
import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services.SoftwareRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BuildscriptVisitor extends BaseBuildFileVisitor {

    private final RepositoriesVisitor repositoriesVisitor;
    private final DependenciesVisitor dependenciesVisitor;
    private final ExtOuterVisitor extOuterVisitor;

    public BuildscriptVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator,
            PluginsVisitor pluginsVisitor, RepositoriesVisitor repositoriesVisitor, DependenciesVisitor dependenciesVisitor, ExtOuterVisitor extOuterVisitor,
            SoftwareRepositoryFactory softwareRepositoryFactory, PluginProcessor pluginProcessor) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, pluginsVisitor, repositoriesVisitor, softwareRepositoryFactory, pluginProcessor);
        this.repositoriesVisitor = repositoriesVisitor;
        this.dependenciesVisitor = dependenciesVisitor;
        this.extOuterVisitor = extOuterVisitor;
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected ExpressionVisitOutcome processMethodCallExpression(MethodCallExpression call) {
        if (call.getMethodAsString().equals("repositories")) {
            if (visitorState().getProcessPhase() == ProcessPhase.BUILDSCRIPT_REPOSITORIES) {
                log.debug("Found buildscript repositories");
                int count = getSoftwareRepositories().size();
                visit(call.getArguments(), repositoriesVisitor);
                log.debug("Found {} repositories", getSoftwareRepositories().size() - count);
                return ExpressionVisitOutcome.PROCESSED;
            } else {
                return ExpressionVisitOutcome.IGNORED_NO_WARNING;
            }
        } else if (call.getMethodAsString().equals("dependencies")) {
            if (visitorState().getProcessPhase() == ProcessPhase.BUILDSCRIPT_DEPENDENCIES) {
                log.debug("Found buildscript dependencies");
                int count = visitorState().getSoftware().size();
                visit(call.getArguments(), dependenciesVisitor);
                log.debug("Found {} dependencies", visitorState().getSoftware().size() - count);
                return ExpressionVisitOutcome.PROCESSED;
            } else {
                return ExpressionVisitOutcome.IGNORED_NO_WARNING;
            }
        } else if (call.getMethodAsString().equals("ext")) {
            if (visitorState().getProcessPhase() == ProcessPhase.PROPERTIES) {
                log.debug("Found buildscript ext");
                int count = visitorState().getProperties().size();
                visit(call, extOuterVisitor);
                log.debug("Found {} project properties", visitorState().getProperties().size() - count);
                return ExpressionVisitOutcome.PROCESSED;
            } else {
                return ExpressionVisitOutcome.IGNORED_NO_WARNING;
            }
        }

        return ExpressionVisitOutcome.IGNORED;
    }
}
