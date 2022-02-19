package tech.kronicle.service.scanners.javaimports;

import org.pf4j.Extension;
import tech.kronicle.sdk.models.Import;
import tech.kronicle.service.constants.Comparators;
import tech.kronicle.service.scanners.CodebaseScanner;
import tech.kronicle.service.scanners.javaimports.internal.services.JavaImportFinder;
import tech.kronicle.service.scanners.models.Codebase;
import tech.kronicle.service.scanners.models.Output;
import tech.kronicle.service.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

@Extension
@RequiredArgsConstructor
public class JavaImportScanner extends CodebaseScanner {

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
    public Output<Void> scan(Codebase input) {
        List<Import> imports = fileUtils.findFileContents(input.getDir(), this::isJavaFile)
                .flatMap(fileContent -> javaImportFinder.findImports(id(), fileContent.getContent()).stream())
                .distinct()
                .sorted(Comparators.IMPORTS)
                .collect(Collectors.toList());
        return Output.of(component -> component.withImports(replaceScannerItemsInList(component.getImports(), imports)));
    }

    private boolean isJavaFile(Path path, BasicFileAttributes attributes) {
        return FilenameUtils.getExtension(path.getFileName().toString()).equals(JAVA_FILE_EXTENSION);
    }
}
