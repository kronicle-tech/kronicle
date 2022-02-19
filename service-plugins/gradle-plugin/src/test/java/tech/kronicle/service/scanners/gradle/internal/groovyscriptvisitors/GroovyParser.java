package tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GroovyParser {

    private final AstBuilder astBuilder = new AstBuilder();

    public List<ASTNode> parseGroovy(String groovy) {
        return astBuilder.buildFromString(groovy);
    }
}
