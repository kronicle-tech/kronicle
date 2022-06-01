package tech.kronicle.plugins.readme;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.CodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.readme.services.ReadmeFileNameChecker;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.utils.FileUtils;
import tech.kronicle.sdk.models.readme.ReadmeState;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ReadmeScanner extends CodebaseScanner {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

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
    public Output<Void, Component> scan(Codebase input) {
        Optional<Path> optionalReadmeFile = fileUtils.findFiles(input.getDir(), SEARCH_ONLY_ROOT_DIRECTORY, this::pathIsReadme)
                .min(PATH_FILE_NAME_COMPARATOR);

        if (optionalReadmeFile.isEmpty()) {
            return Output.empty(CACHE_TTL);
        }

        return Output.ofTransformer(
                component -> component.addState(createReadmeState(optionalReadmeFile.get())),
                CACHE_TTL
        );
    }

    private ReadmeState createReadmeState(Path readmeFile) {
        return new ReadmeState(
                ReadmePlugin.ID,
                readmeFile.getFileName().toString(),
                fileUtils.readFileContent(readmeFile)
        );
    }

    private boolean pathIsReadme(Path path, BasicFileAttributes attributes) {
        return attributes.isRegularFile() && readmeFileNameChecker.fileNameIsReadmeFileName(path);
    }
}
