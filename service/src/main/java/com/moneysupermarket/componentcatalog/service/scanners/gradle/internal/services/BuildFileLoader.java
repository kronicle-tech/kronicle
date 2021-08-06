package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services;

import com.moneysupermarket.componentcatalog.service.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.CompilePhase;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.moneysupermarket.componentcatalog.common.utils.StringEscapeUtils.escapeString;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuildFileLoader {

    private final AstBuilder astBuilder = new AstBuilder();
    private final FileUtils fileUtils;
    private final BuildFileCache buildFileCache;
    private final PropertyExpander propertyExpander;

    public List<ASTNode> loadBuildFile(Path buildFile, Path codebaseDir) {
        if (!buildFile.startsWith(codebaseDir)) {
            throw new IllegalArgumentException(String.format("buildFile path \"%s\" is not within codebaseDir path \"%s\"", escapeString(buildFile.toString()),
                    escapeString(codebaseDir.toString())));
        }
        String contents = fileUtils.readFileContent(buildFile);
        if (contents.isEmpty()) {
            return List.of();
        } else {
            List<ASTNode> nodes = buildFileCache.getBuildFileNodes(contents);

            if (nonNull(nodes)) {
                log.debug("Used cached nodes for build file \"{}\"", buildFile);
            } else {
                log.debug("Built new nodes for build file \"{}\"", buildFile);
                nodes = astBuilder.buildFromString(CompilePhase.CONVERSION, false, contents);
                buildFileCache.putBuildFileNodes(contents, nodes);
            }

            return nodes;
        }
    }

    public Path resolveApplyFromFile(String applyFromFileName, Path buildFile, Map<String, String> properties) {
        applyFromFileName = propertyExpander.expandProperties(applyFromFileName, "applyFromFileName", properties, false);
        return buildFile.getParent().resolve(Path.of(applyFromFileName));
    }
}
