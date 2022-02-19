package tech.kronicle.service.scanners.gradle.internal.services;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.codehaus.groovy.ast.ASTNode;

import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class BuildFileCache {

    private static final HashFunction HASH_FUNCTION = Hashing.md5();
    private Map<String, List<ASTNode>> cache = new HashMap<>();

    public List<ASTNode> getBuildFileNodes(String content) {
        return cache.get(getHashCodeForBuildFileContent(content));
    }

    public List<ASTNode> putBuildFileNodes(String content, List<ASTNode> nodes) {
        return cache.put(getHashCodeForBuildFileContent(content), nodes);
    }

    private String getHashCodeForBuildFileContent(String content) {
        return HASH_FUNCTION.hashString(content, StandardCharsets.UTF_8).toString();
    }
}
