package tech.kronicle.plugins.gradle.internal.groovyscriptvisitors;

import tech.kronicle.sdk.models.SoftwareRepository;
import tech.kronicle.sdk.models.SoftwareRepositoryScope;
import tech.kronicle.plugins.gradle.internal.services.BuildFileLoader;
import tech.kronicle.plugins.gradle.internal.services.BuildFileProcessor;
import tech.kronicle.plugins.gradle.internal.services.ExpressionEvaluator;
import tech.kronicle.plugins.gradle.internal.services.SoftwareRepositoryFactory;
import tech.kronicle.plugins.gradle.internal.utils.InheritingHashMap;
import tech.kronicle.plugins.gradle.internal.utils.InheritingHashSet;
import lombok.RequiredArgsConstructor;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.LambdaExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.MethodReferenceExpression;
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.slf4j.Logger;
import tech.kronicle.common.utils.StringEscapeUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public abstract class BaseVisitor extends CodeVisitorSupport {

    private final BuildFileLoader buildFileLoader;
    private final BuildFileProcessor buildFileProcessor;
    private final ExpressionEvaluator expressionEvaluator;
    private final SoftwareRepositoryFactory softwareRepositoryFactory;
    private VisitorState visitorState;
    private InheritingHashMap<String, String> variables;

    protected abstract Logger log();

    protected final ExpressionEvaluator expressionEvaluator() {
        return expressionEvaluator;
    }

    protected final SoftwareRepositoryFactory softwareRepositoryFactory() {
        return softwareRepositoryFactory;
    }

    protected final VisitorState visitorState() {
        return visitorState;
    }

    public final void setVisitorState(VisitorState visitorState, InheritingHashMap<String, String> variables) {
        this.visitorState = visitorState;
        this.variables = new InheritingHashMap<>(variables);
    }

    protected String evaluateExpression(Expression expression) {
        return expressionEvaluator.evaluateExpression(expression, variables, visitorState().getImports());
    }

    protected final void visit(Expression expression, BaseVisitor visitor) {
        visitor.setVisitorState(visitorState, variables);
        expression.visit(visitor);
    }

    @Override
    public final void visitMethodCallExpression(MethodCallExpression call) {
        handleOutcome(
                call,
                this::processApply,
                ignored -> handleOutcome(call, this::processMethodCallExpression, super::visitMethodCallExpression, call.getMethodAsString()),
                call.getMethodAsString());
    }

    protected ExpressionVisitOutcome processMethodCallExpression(MethodCallExpression call) {
        return ExpressionVisitOutcome.CONTINUE;
    }

    @Override
    public final void visitBinaryExpression(BinaryExpression expression) {
        handleOutcome(expression, this::processBinaryExpression, super::visitBinaryExpression, null);
    }

    protected ExpressionVisitOutcome processBinaryExpression(BinaryExpression expression) {
        if (expression instanceof DeclarationExpression) {
            log().debug("Found declaration");
            processDeclarationExpression((DeclarationExpression) expression);
            return ExpressionVisitOutcome.PROCESSED;
        }

        return ExpressionVisitOutcome.CONTINUE;
    }

    protected void processDeclarationExpression(DeclarationExpression expression) {
        variables.put(expression.getLeftExpression().getText(), evaluateExpression(expression.getRightExpression()));
    }

    protected ExpressionVisitOutcome processAssignment(BinaryExpression expression) {
        if (expression.getLeftExpression() instanceof PropertyExpression) {
            visitorState().getProperties().put(
                    ((PropertyExpression) expression.getLeftExpression()).getProperty().getText(),
                    evaluateExpression(expression.getRightExpression()));
            return ExpressionVisitOutcome.PROCESSED;
        }
        return ExpressionVisitOutcome.IGNORED;
    }

    private <T extends Expression> void handleOutcome(T expression, Function<T, ExpressionVisitOutcome> thisMethod, Consumer<T> superMethod, String name) {
        ExpressionVisitOutcome outcome = thisMethod.apply(expression);

        switch (outcome) {
            case PROCESSED:
            case IGNORED_NO_WARNING:
                return;
            case IGNORED:
                if (nonNull(name)) {
                    log().info("{} {} ignored", expression.getClass().getName(), name);
                } else {
                    log().info("{} ignored", expression.getClass().getName());
                }
                return;
            case CONTINUE:
                superMethod.accept(expression);
                return;
            default:
                throw new RuntimeException(String.format("Unexpected outcome %s", outcome));
        }
    }

    protected void addSoftwareRepository(String url) {
        getSoftwareRepositories().add(softwareRepositoryFactory.createSoftwareRepository(visitorState.getScannerId(), url, getSoftwareRepositoryScope()));
    }

    private SoftwareRepositoryScope getSoftwareRepositoryScope() {
        switch (visitorState.getProcessPhase()) {
            case BUILDSCRIPT_REPOSITORIES:
                return SoftwareRepositoryScope.BUILDSCRIPT;
            case REPOSITORIES:
                return null;
            default:
                throw new RuntimeException("Unexpected process phase " + visitorState.getProcessPhase());
        }
    }

    protected InheritingHashSet<SoftwareRepository> getSoftwareRepositories() {
        switch (visitorState.getProcessPhase()) {
            case PLUGINS:
            case BUILDSCRIPT_REPOSITORIES:
            case BUILDSCRIPT_DEPENDENCIES:
            case APPLY_PLUGINS:
                return visitorState.getBuildscriptSoftwareRepositories();
            case REPOSITORIES:
            case DEPENDENCY_MANAGEMENT:
            case DEPENDENCIES:
                return visitorState.getSoftwareRepositories();
            default:
                throw new RuntimeException("Unexpected process phase " + visitorState.getProcessPhase());
        }
    }

    private ExpressionVisitOutcome processApply(MethodCallExpression call) {
        if (call.getMethodAsString().equals("apply")) {
            log().debug("Found apply");
            if (call.getArguments() instanceof TupleExpression) {
                TupleExpression arguments = (TupleExpression) call.getArguments();
                if (arguments.getExpressions().size() == 1 && arguments.getExpression(0) instanceof NamedArgumentListExpression) {
                    NamedArgumentListExpression namedArguments = (NamedArgumentListExpression) arguments.getExpression(0);
                    Map<String, String> values = namedArguments.getMapEntryExpressions().stream()
                            .collect(Collectors.toMap(
                                    entry -> evaluateExpression(entry.getKeyExpression()),
                                    entry -> evaluateExpression(entry.getValueExpression())));

                    if (values.size() == 1 && values.containsKey("plugin")) {
                        if (visitorState.getProcessPhase() == ProcessPhase.APPLY_PLUGINS) {
                            processApplyPlugin(values);
                            return ExpressionVisitOutcome.PROCESSED;
                        } else {
                            return ExpressionVisitOutcome.IGNORED_NO_WARNING;
                        }
                    } else if ((values.size() == 1 && values.containsKey("from")) || (values.size() == 2 && values.containsKey("from") && values.containsKey(
                            "to"))) {
                        String from = values.get("from");
                        Path applyFile = buildFileLoader.resolveApplyFromFile(from, visitorState.getBuildFile(), variables);
                        log().debug("Processing file \"{}\"", StringEscapeUtils.escapeString(visitorState.getCodebaseDir().relativize(applyFile).toString()));
                        List<ASTNode> nodes = buildFileLoader.loadBuildFile(applyFile, visitorState.getCodebaseDir());
                        VisitorState oldVisitorState = visitorState;
                        visitorState = visitorState.withApplyFile(applyFile);
                        buildFileProcessor.visitNodes(nodes, this);
                        visitorState = oldVisitorState;
                        log().debug("Finished processing file \"{}\"", StringEscapeUtils.escapeString(from));
                        return ExpressionVisitOutcome.PROCESSED;
                    } else {
                        throw new RuntimeException(String.format("apply call with unexpected arguments %s", values.keySet().stream().collect(Collectors.joining(", "))));
                    }
                }
            }
        }

        return ExpressionVisitOutcome.CONTINUE;
    }

    protected void processApplyPlugin(Map<String, String> values) {
    }

    @Override
    public final void visitBlockStatement(BlockStatement block) {
        super.visitBlockStatement(block);
    }

    @Override
    public final void visitForLoop(ForStatement forLoop) {
        super.visitForLoop(forLoop);
    }

    @Override
    public final void visitWhileLoop(WhileStatement loop) {
        super.visitWhileLoop(loop);
    }

    @Override
    public final void visitDoWhileLoop(DoWhileStatement loop) {
        super.visitDoWhileLoop(loop);
    }

    @Override
    public final void visitIfElse(IfStatement ifElse) {
        super.visitIfElse(ifElse);
    }

    @Override
    public final void visitExpressionStatement(ExpressionStatement statement) {
        super.visitExpressionStatement(statement);
    }

    @Override
    public final void visitReturnStatement(ReturnStatement statement) {
        super.visitReturnStatement(statement);
    }

    @Override
    public final void visitAssertStatement(AssertStatement statement) {
        super.visitAssertStatement(statement);
    }

    @Override
    public final void visitTryCatchFinally(TryCatchStatement statement) {
        super.visitTryCatchFinally(statement);
    }

    @Override
    public final void visitEmptyStatement(EmptyStatement statement) {
        super.visitEmptyStatement(statement);
    }

    @Override
    public final void visitSwitch(SwitchStatement statement) {
        super.visitSwitch(statement);
    }

    @Override
    protected void afterSwitchConditionExpressionVisited(SwitchStatement statement) {
        super.afterSwitchConditionExpressionVisited(statement);
    }

    @Override
    public final void visitCaseStatement(CaseStatement statement) {
        super.visitCaseStatement(statement);
    }

    @Override
    public final void visitBreakStatement(BreakStatement statement) {
        super.visitBreakStatement(statement);
    }

    @Override
    public final void visitContinueStatement(ContinueStatement statement) {
        super.visitContinueStatement(statement);
    }

    @Override
    public final void visitSynchronizedStatement(SynchronizedStatement statement) {
        super.visitSynchronizedStatement(statement);
    }

    @Override
    public final void visitThrowStatement(ThrowStatement statement) {
        super.visitThrowStatement(statement);
    }

    @Override
    public final void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        super.visitStaticMethodCallExpression(call);
    }

    @Override
    public final void visitConstructorCallExpression(ConstructorCallExpression call) {
        super.visitConstructorCallExpression(call);
    }

    @Override
    public final void visitTernaryExpression(TernaryExpression expression) {
        super.visitTernaryExpression(expression);
    }

    @Override
    public final void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        super.visitShortTernaryExpression(expression);
    }

    @Override
    public final void visitPostfixExpression(PostfixExpression expression) {
        super.visitPostfixExpression(expression);
    }

    @Override
    public final void visitPrefixExpression(PrefixExpression expression) {
        super.visitPrefixExpression(expression);
    }

    @Override
    public final void visitBooleanExpression(BooleanExpression expression) {
        super.visitBooleanExpression(expression);
    }

    @Override
    public final void visitNotExpression(NotExpression expression) {
        super.visitNotExpression(expression);
    }

    @Override
    public final void visitClosureExpression(ClosureExpression expression) {
        super.visitClosureExpression(expression);
    }

    @Override
    public final void visitLambdaExpression(LambdaExpression expression) {
        super.visitLambdaExpression(expression);
    }

    @Override
    public final void visitTupleExpression(TupleExpression expression) {
        super.visitTupleExpression(expression);
    }

    @Override
    public final void visitListExpression(ListExpression expression) {
        super.visitListExpression(expression);
    }

    @Override
    public final void visitArrayExpression(ArrayExpression expression) {
        super.visitArrayExpression(expression);
    }

    @Override
    public final void visitMapExpression(MapExpression expression) {
        super.visitMapExpression(expression);
    }

    @Override
    public final void visitMapEntryExpression(MapEntryExpression expression) {
        super.visitMapEntryExpression(expression);
    }

    @Override
    public final void visitRangeExpression(RangeExpression expression) {
        super.visitRangeExpression(expression);
    }

    @Override
    public final void visitSpreadExpression(SpreadExpression expression) {
        super.visitSpreadExpression(expression);
    }

    @Override
    public final void visitSpreadMapExpression(SpreadMapExpression expression) {
        super.visitSpreadMapExpression(expression);
    }

    @Override
    public final void visitMethodPointerExpression(MethodPointerExpression expression) {
        super.visitMethodPointerExpression(expression);
    }

    @Override
    public final void visitMethodReferenceExpression(MethodReferenceExpression expression) {
        super.visitMethodReferenceExpression(expression);
    }

    @Override
    public final void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        super.visitUnaryMinusExpression(expression);
    }

    @Override
    public final void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        super.visitUnaryPlusExpression(expression);
    }

    @Override
    public final void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        super.visitBitwiseNegationExpression(expression);
    }

    @Override
    public final void visitCastExpression(CastExpression expression) {
        super.visitCastExpression(expression);
    }

    @Override
    public final void visitConstantExpression(ConstantExpression expression) {
        super.visitConstantExpression(expression);
    }

    @Override
    public final void visitClassExpression(ClassExpression expression) {
        super.visitClassExpression(expression);
    }

    @Override
    public final void visitVariableExpression(VariableExpression expression) {
        super.visitVariableExpression(expression);
    }

    @Override
    public final void visitDeclarationExpression(DeclarationExpression expression) {
        super.visitDeclarationExpression(expression);
    }

    @Override
    public final void visitPropertyExpression(PropertyExpression expression) {
        super.visitPropertyExpression(expression);
    }

    @Override
    public final void visitAttributeExpression(AttributeExpression expression) {
        super.visitAttributeExpression(expression);
    }

    @Override
    public final void visitFieldExpression(FieldExpression expression) {
        super.visitFieldExpression(expression);
    }

    @Override
    public final void visitGStringExpression(GStringExpression expression) {
        super.visitGStringExpression(expression);
    }

    @Override
    public final void visitCatchStatement(CatchStatement statement) {
        super.visitCatchStatement(statement);
    }

    @Override
    public final void visitArgumentlistExpression(ArgumentListExpression expression) {
        super.visitArgumentlistExpression(expression);
    }

    @Override
    public final void visitClosureListExpression(ClosureListExpression expression) {
        super.visitClosureListExpression(expression);
    }

    @Override
    public final void visitBytecodeExpression(BytecodeExpression expression) {
        super.visitBytecodeExpression(expression);
    }

    @Override
    public final void visitEmptyExpression(EmptyExpression expression) {
        super.visitEmptyExpression(expression);
    }

    @Override
    public final void visitListOfExpressions(List<? extends Expression> list) {
        super.visitListOfExpressions(list);
    }
}
