package tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.buildgradlevisitor;

import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.BaseVisitor;
import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.ExpressionVisitOutcome;
import tech.kronicle.plugins.gradle.internal.services.BuildFileLoader;
import tech.kronicle.plugins.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.plugins.gradle.internal.services.BillOfMaterialsLogger;
import tech.kronicle.plugins.gradle.internal.services.DependencyVersionFetcher;
import tech.kronicle.plugins.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.plugins.gradle.internal.services.SoftwareRepositoryFactory;
import tech.kronicle.plugins.gradle.internal.utils.ArtifactUtils;
import tech.kronicle.service.utils.ObjectReference;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;

import java.util.Objects;

public abstract class BaseArtifactVisitor extends BaseVisitor {

    private final ArtifactUtils artifactUtils;
    private final DependencyVersionFetcher dependencyVersionFetcher;
    private final BillOfMaterialsLogger billOfMaterialsLogger;

    public BaseArtifactVisitor(BuildFileLoader buildFileLoader, BuildFileProcessor buildFileProcessor, ExpressionEvaluator expressionEvaluator,
                               SoftwareRepositoryFactory softwareRepositoryFactory, ArtifactUtils artifactUtils,
                               DependencyVersionFetcher dependencyVersionFetcher, BillOfMaterialsLogger billOfMaterialsLogger) {
        super(buildFileLoader, buildFileProcessor, expressionEvaluator, softwareRepositoryFactory);
        this.artifactUtils = artifactUtils;
        this.dependencyVersionFetcher = dependencyVersionFetcher;
        this.billOfMaterialsLogger = billOfMaterialsLogger;
    }

    protected ArtifactUtils artifactUtils() {
        return artifactUtils;
    }

    @Override
    protected final ExpressionVisitOutcome processMethodCallExpression(MethodCallExpression call) {
        if (shouldProcessArguments(call)) {
            processArguments(call);
            return ExpressionVisitOutcome.PROCESSED;
        }

        return ExpressionVisitOutcome.IGNORED;
    }

    protected boolean shouldProcessArguments(MethodCallExpression call) {
        return Objects.equals(call.getObjectExpression().getText(), "this");
    }

    protected final void processArguments(MethodCallExpression call) {
        if (call.getArguments() instanceof ArgumentListExpression) {
            ((ArgumentListExpression) call.getArguments()).getExpressions()
                    .forEach(argument -> {
                        if (argument instanceof ConstantExpression) {
                            processArtifact((ConstantExpression) argument);
                        } else if (argument instanceof GStringExpression) {
                            processArtifact((GStringExpression) argument);
                        } else if (argument instanceof MapExpression) {
                            processArtifact((MapExpression) argument);
                        } else if (argument instanceof BinaryExpression) {
                            processArtifact((BinaryExpression) argument);
                        } else if (argument instanceof ListExpression) {
                            processArtifact((ListExpression) argument);
                        } else if (argument instanceof MethodCallExpression) {
                            MethodCallExpression methodCallExpression = (MethodCallExpression) argument;
                            String methodName = methodCallExpression.getMethodAsString();

                            if (methodName.equals("platform")) {
                                processPlatform(methodCallExpression);
                            } else if (methodName.equals("project") || methodName.equals("localGroovy") || methodName.equals("gradleApi")
                                    || methodName.equals("files") || methodName.equals("fileTree")) {
                                // Do nothing
                            } else {
                                throw new RuntimeException("Unsupported method name " + methodName);
                            }
                        } else if (argument instanceof ClosureExpression || argument instanceof PropertyExpression) {
                            // Do nothing
                        } else {
                            throw new RuntimeException("Unsupported argument type " + argument.getClass().getName());
                        }
                    });
        } else if (call.getArguments() instanceof TupleExpression) {
            processArtifact((MapExpression) ((TupleExpression) call.getArguments()).getExpression(0));
        } else {
            throw new RuntimeException("Unsupported arguments type " + call.getArguments().getClass().getName());
        }
    }

    protected void processPlatform(MethodCallExpression call) {
    }

    protected abstract void addArtifact(String groupId, String artifactId, String version, String packaging);
    
    protected void addBillOfMaterialsArtifact(String groupId, String artifactId, String version, String packaging) {
        billOfMaterialsLogger.logManagedDependencies(visitorState(), () -> {
            String artifact = artifactUtils().createArtifact(groupId, artifactId, version, packaging);
            dependencyVersionFetcher.findDependencyVersions(
                    visitorState().getScannerId(),
                    artifact,
                    getSoftwareRepositories(),
                    visitorState().getDependencyVersions(),
                    visitorState().getSoftware());
        });
    }

    private void processArtifact(ConstantExpression constant) {
        processArtifact(evaluateExpression(constant));
    }

    private void processArtifact(GStringExpression gstring) {
        processArtifact(evaluateExpression(gstring));
    }

    private void processArtifact(BinaryExpression binary) {
        processArtifact(evaluateExpression(binary));
    }

    private void processArtifact(ListExpression list) {
        list.getExpressions().forEach(expression -> processArtifact(evaluateExpression(expression)));
    }

    private void processArtifact(MapExpression map) {
        ObjectReference<String> groupId = new ObjectReference<>();
        ObjectReference<String> artifactId = new ObjectReference<>();
        ObjectReference<String> version = new ObjectReference<>();

        try {
            map.getMapEntryExpressions()
                    .forEach(entry -> {
                        String key = evaluateExpression(entry.getKeyExpression());
                        String value = evaluateExpression(entry.getValueExpression());

                        switch (key) {
                            case "group":
                                groupId.set(value);
                                break;
                            case "name":
                                artifactId.set(value);
                                break;
                            case "version":
                                version.set(value);
                                break;
                            case "classifier":
                            case "ext":
                                // Do nothing
                                break;
                            default:
                                throw new RuntimeException("Unexpected artifact key \"" + key + "\"");
                        }
                    });

            processArtifact(groupId.get(), artifactId.get(), version.get(), null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create software item for artifact", e);
        }
    }

    private void processArtifact(String artifact) {
        String[] artifactParts = artifact.split(":");

        if (artifactParts.length < 2 || artifactParts.length > 4) {
            throw new RuntimeException("Unexpected format of artifact");
        }

        String groupId = artifactParts[0];
        String artifactId = artifactParts[1];
        String version = artifactParts.length >= 3 ? artifactParts[2] : null;
        String packaging = artifactParts.length >= 4 ? artifactParts[3] : null;
        processArtifact(groupId, artifactId, version, packaging);
    }

    private void processArtifact(String groupId, String artifactId, String version, String packaging) {
        try {
            addArtifact(groupId, artifactId, version, packaging);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create software item for artifact", e);
        }
    }
}
