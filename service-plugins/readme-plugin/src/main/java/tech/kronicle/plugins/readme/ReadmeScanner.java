package tech.kronicle.plugins.readme;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import tech.kronicle.pluginapi.scanners.CodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.readme.services.ReadmeFileNameChecker;
import tech.kronicle.pluginutils.utils.FileUtils;
import tech.kronicle.sdk.models.readme.Readme;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Optional;

@Extension
@Component
@RequiredArgsConstructor
public class ReadmeScanner extends CodebaseScanner {

    private static final int SEARCH_ONLY_ROOT_DIRECTORY = 1;
    private static final Comparator<Path> PATH_FILE_NAME_COMPARATOR = Comparator.comparing(path -> path.getFileName().toString());

    private final FileUtils fileUtils;
    private final ReadmeFileNameChecker readmeFileNameChecker;

    @Override
    public String id() {
        return "readme";
    }

    @Override
    public String description() {
        return "Scans a component's codebase for a README file at the root of the codebase";
    }

    @Override
    public Output<Void> scan(Codebase input) {
        Optional<Path> optionalReadmeFile = fileUtils.findFiles(input.getDir(), SEARCH_ONLY_ROOT_DIRECTORY, this::pathIsReadme)
                .sorted(PATH_FILE_NAME_COMPARATOR)
                .findFirst();

        Readme readme = optionalReadmeFile
                .map(readmeFile -> new Readme(readmeFile.getFileName().toString(), fileUtils.readFileContent(readmeFile)))
                .orElse(null);
        return Output.of(component -> component.withReadme(readme));
    }

    private boolean pathIsReadme(Path path, BasicFileAttributes attributes) {
        return attributes.isRegularFile() && readmeFileNameChecker.fileNameIsReadmeFileName(path);
    }
}
