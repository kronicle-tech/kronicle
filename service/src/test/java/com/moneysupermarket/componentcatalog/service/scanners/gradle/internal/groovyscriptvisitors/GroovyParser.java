package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.groovyscriptvisitors;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroovyParser {

    private final AstBuilder astBuilder = new AstBuilder();

    public List<ASTNode> parseGroovy(String groovy) {
        return astBuilder.buildFromString(groovy);
    }
}
