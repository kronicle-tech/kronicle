package tech.kronicle.plugins.linesofcode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.CodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.linesofcode.services.LinesOfCodeCounter;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.utils.FileUtils;
import tech.kronicle.utils.ObjectReference;
import tech.kronicle.sdk.models.linesofcode.FileExtensionCount;
import tech.kronicle.sdk.models.linesofcode.LinesOfCodeState;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class LinesOfCodeScanner extends CodebaseScanner {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    private final FileUtils fileUtils;
    private final LinesOfCodeCounter linesOfCodeCounter;

    @Override
    public String id() {
        return "lines-of-code";
    }

    @Override
    public String description() {
        return "Scans a component's codebase, finding all the file extensions for textual files in the codebase and calculates the total number of lines of "
                + "text for each of those file extensions";
    }

    @Override
    public Output<Void, Component> scan(Codebase input) {
        LinesOfCodeState linesOfCode = getLinesOfCode(input);

        return Output.ofTransformer(
                component -> component.addState(linesOfCode),
                CACHE_TTL
        );
    }

    private LinesOfCodeState getLinesOfCode(Codebase codebase) {
        log.debug("Counting lines of code for repo \"{}\"", codebase.getRepo().getUrl());

        ObjectReference<Integer> count = new ObjectReference<>(0);
        Map<String, Integer> fileExtensionCountMap = new HashMap<>();

        fileUtils.findFiles(codebase.getDir())
                .forEach(file -> {
                    LinesOfCodeCounter.LinesOfCodeCountResult result;
                    try {
                        try (InputStream contentIn = fileUtils.openFile(file)) {
                            result = linesOfCodeCounter.countLinesOfCode(contentIn);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    log.trace("File \"{}\" is of type {} and contains {} lines of code", codebase.getDir().relativize(file), result.getFileType(),
                            result.getLinesOfCodeCount());
                    count.set(count.get() + result.getLinesOfCodeCount());
                    addToFileExtensionCounts(fileExtensionCountMap, file, result);
                });

        log.debug("Repo \"{}\" contains {} lines of code", codebase.getRepo().getUrl(), count.get());

        List<FileExtensionCount> fileExtensionCounts = fileExtensionCountMap.entrySet().stream()
                .map(entry -> new FileExtensionCount(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(FileExtensionCount::getCount).reversed())
                .collect(Collectors.toList());

        fileExtensionCounts.forEach(fileExtensionCount -> {
            if (fileExtensionCount.getFileExtension().isEmpty()) {
                log.debug("Repo \"{}\" contains {} lines of code with no file extension", codebase.getRepo().getUrl(), fileExtensionCount.getCount());
            } else {
                log.debug("Repo \"{}\" contains {} lines of code with \".{}\" file extension", codebase.getRepo().getUrl(), fileExtensionCount.getCount(),
                        fileExtensionCount.getFileExtension());
            }
        });

        return new LinesOfCodeState(LinesOfCodePlugin.ID, count.get(), fileExtensionCounts);
    }

    private void addToFileExtensionCounts(Map<String, Integer> fileExtensionCounts, Path file, LinesOfCodeCounter.LinesOfCodeCountResult result) {
        String fileExtension = FilenameUtils.getExtension(file.getFileName().toString());
        Integer fileExtensionCount = fileExtensionCounts.get(fileExtension);

        if (isNull(fileExtensionCount)) {
            fileExtensionCount = 0;
        }

        fileExtensionCount += result.getLinesOfCodeCount();
        fileExtensionCounts.put(fileExtension, fileExtensionCount);
    }
}
