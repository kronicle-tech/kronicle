package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services;

import com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.models.Import;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpressionEvaluator {

    private final PropertyExpander propertyExpander;
    private final PropertyRetriever propertyRetriever;
    private final ImportResolver importResolver;

    public String evaluateExpression(Expression expression, Map<String, String> properties, Set<Import> imports) {
        if (expression instanceof ConstantExpression) {
            return expression.getText();
        } else if (expression instanceof GStringExpression) {
            return propertyExpander.expandProperties(expression.getText(), "expression", properties, false);
        } else if (expression instanceof VariableExpression) {
            String name = expression.getText();
            if (Objects.equals(name, "buildscript")) {
                return name;
            } else {
                Import importItem = importResolver.importResolver(name, imports);
                if (nonNull(importItem)) {
                    return importItem.getClassName();
                }
                return propertyRetriever.getPropertyValue(name, properties);
            }
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression binary = (BinaryExpression) expression;
            String operationText = binary.getOperation().getText();

            if (operationText.equals("+")) {
                return evaluateExpression(binary.getLeftExpression(), properties, imports)
                        + evaluateExpression(binary.getRightExpression(), properties, imports);
            } else {
                log.debug("Ignored binary expression with operation \"" + operationText + "\"");
                return null;
            }
        } else if (expression instanceof PropertyExpression) {
            return expression.getText();
        } else {
            log.debug("Ignored binary expression of type \"" + expression.getClass().getName() + "\"");
            return null;
        }
    }
}
