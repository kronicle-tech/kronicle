package tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import tech.kronicle.service.scanners.gradle.internal.constants.SoftwareRepositoryUrls;
import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.BaseVisitor;
import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.ExpressionVisitOutcome;
import tech.kronicle.service.scanners.gradle.internal.services.BuildFileLoader;
import tech.kronicle.service.scanners.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.service.scanners.gradle.internal.services.CustomRepositoryRegistry;
import tech.kronicle.service.scanners.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.service.scanners.gradle.internal.services.SoftwareRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
@Slf4j
public class RepositoriesVisitor extends BaseVisitor {

    private final MavenRepositoryVisitor mavenRepositoryVisitor;
    private final CustomRepositoryRegistry customRepositoryRegistry;

    public RepositoriesVisitor(
            BuildFileLoader buildFileLoader, 
            BuildFileProcessor buildFileProcessor, 
            ExpressionEvaluator expressionEvaluator, 
            MavenRepositoryVisitor mavenRepositoryVisitor, 
            SoftwareRepositoryFactory softwareRepositoryFactory, 
            CustomRepositoryRegistry customRepositoryRegistry
    ) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, softwareRepositoryFactory);
        this.mavenRepositoryVisitor = mavenRepositoryVisitor;
        this.customRepositoryRegistry = customRepositoryRegistry;
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected ExpressionVisitOutcome processMethodCallExpression(MethodCallExpression call) {
        String methodName = call.getMethodAsString();

        if (methodName.equals("maven")) {
            visit(call.getArguments(), mavenRepositoryVisitor);
            return ExpressionVisitOutcome.PROCESSED;
        }

        String url = getRepositoryUrl(methodName);

        if (nonNull(url)) {
            addSoftwareRepository(url);
            return ExpressionVisitOutcome.PROCESSED;
        }

        return ExpressionVisitOutcome.IGNORED;
    }

    private String getRepositoryUrl(String methodName) {
        if (methodName.equals("gradlePluginPortal")) {
            return SoftwareRepositoryUrls.GRADLE_PLUGIN_PORTAL;
        } else if (methodName.equals("mavenCentral")) {
            return SoftwareRepositoryUrls.MAVEN_CENTRAL;
        } else if (methodName.equals("jcenter")) {
            return SoftwareRepositoryUrls.JCENTER;
        } else if (methodName.equals("google")) {
            return SoftwareRepositoryUrls.GOOGLE;
        } else {
            return customRepositoryRegistry.getCustomRepositoryUrl(methodName);
        }
    }
}
