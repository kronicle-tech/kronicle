package tech.kronicle.plugins.javaimport;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.CodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.javaimport.services.JavaImportFinder;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ImportsState;
import tech.kronicle.utils.Comparators;
import tech.kronicle.utils.FileUtils;
import tech.kronicle.sdk.models.Import;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class JavaImportScanner extends CodebaseScanner {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    public static final String JAVA_FILE_EXTENSION = "java";
    private final FileUtils fileUtils;
    private final JavaImportFinder javaImportFinder;

    @Override
    public String id() {
        return "java-import";
    }

    @Override
    public String description() {
        return "Scans a component's codebase and finds the names of all Java types imported by Java import statements";
    }

    @Override
    public Output<Void, Component> scan(Codebase input) {
        List<Import> imports = fileUtils.findFileContents(input.getDir(), this::isJavaFile)
                .flatMap(fileContent -> javaImportFinder.findImports(id(), fileContent.getContent()).stream())
                .distinct()
                .sorted(Comparators.IMPORTS)
                .collect(Collectors.toList());

        if (imports.isEmpty()) {
            return Output.empty(CACHE_TTL);
        }

        return Output.ofTransformer(
                component -> component.addState(new ImportsState(JavaImportPlugin.ID, imports)),
                CACHE_TTL
        );
    }

    private boolean isJavaFile(Path path, BasicFileAttributes attributes) {
        return FilenameUtils.getExtension(path.getFileName().toString()).equals(JAVA_FILE_EXTENSION);
    }
}
