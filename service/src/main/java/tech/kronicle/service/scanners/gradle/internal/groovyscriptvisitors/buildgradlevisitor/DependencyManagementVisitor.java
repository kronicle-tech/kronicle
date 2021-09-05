package tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import tech.kronicle.sdk.models.SoftwareDependencyType;
import tech.kronicle.sdk.models.SoftwareType;
import tech.kronicle.service.scanners.gradle.internal.constants.MavenPackagings;
import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.BaseVisitor;
import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.ExpressionVisitOutcome;
import tech.kronicle.service.scanners.gradle.internal.services.BuildFileLoader;
import tech.kronicle.service.scanners.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.service.scanners.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.service.scanners.gradle.internal.services.SoftwareRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class DependencyManagementVisitor extends BaseVisitor {

    private final DependencyManagementImportsVisitor dependencyManagementImportsVisitor;

    public DependencyManagementVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator,
                                       DependencyManagementImportsVisitor dependencyManagementImportsVisitor, SoftwareRepositoryFactory softwareRepositoryFactory) {
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
            int directDependencyCount = getDirectDependencyCount();
            int transitiveDependencyCount = getTransitiveDependencyCount();
            int dependencyVersionCount = visitorState().getDependencyVersions().size();
            visit(call.getArguments(), dependencyManagementImportsVisitor);
            log.debug("Found {} direct bill of materials", getDirectDependencyCount() - directDependencyCount);
            log.debug("Found {} transitive bill of materials", getTransitiveDependencyCount() - transitiveDependencyCount);
            log.debug("Found {} dependency versions", visitorState().getDependencyVersions().size() - dependencyVersionCount);
            return ExpressionVisitOutcome.PROCESSED;
        }

        return ExpressionVisitOutcome.IGNORED;
    }

    private int getDirectDependencyCount() {
        return getDependencyCount(SoftwareDependencyType.DIRECT);
    }

    private int getTransitiveDependencyCount() {
        return getDependencyCount(SoftwareDependencyType.TRANSITIVE);
    }

    private int getDependencyCount(SoftwareDependencyType dependencyType) {
        return (int) visitorState().getSoftware().stream()
                .filter(software -> Objects.equals(software.getType(), SoftwareType.JVM)
                        && Objects.equals(software.getPackaging(), MavenPackagings.BOM)
                        && Objects.equals(software.getDependencyType(), dependencyType))
                .count();
    }
}
