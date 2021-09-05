package tech.kronicle.service.scanners.gradle.internal.services;

import tech.kronicle.service.scanners.gradle.internal.groovyscriptvisitors.BaseVisitor;
import tech.kronicle.service.scanners.gradle.internal.models.Import;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BuildFileProcessor {

    public Set<Import> getImports(List<ASTNode> nodes) {
        Set<Import> imports = new HashSet<>();

        nodes.forEach(node -> {
            if (isClassNode(node)) {
                ClassNode classNode = (ClassNode) node;
                classNode.getModule().getImports().forEach(importNode -> {
                    if (!importNode.isStar() && !importNode.isStatic()) {
                        imports.add(new Import(importNode.getClassName(), importNode.getAlias()));
                    }
                });
            }
        });

        return imports;
    }

    public void visitNodes(List<ASTNode> nodes, BaseVisitor visitor) {
        nodes.forEach(node -> {
            if (!(isClassNode(node))) {
                node.visit(visitor);
            }
        });
    }

    private boolean isClassNode(ASTNode node) {
        return node instanceof ClassNode;
    }
}
