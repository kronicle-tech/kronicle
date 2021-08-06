package com.moneysupermarket.componentcatalog.service.scanners.linesofcode;

import com.moneysupermarket.componentcatalog.sdk.models.linesofcode.FileExtensionCount;
import com.moneysupermarket.componentcatalog.sdk.models.linesofcode.LinesOfCode;
import com.moneysupermarket.componentcatalog.service.scanners.CodebaseScanner;
import com.moneysupermarket.componentcatalog.service.scanners.linesofcode.services.LinesOfCodeCounter;
import com.moneysupermarket.componentcatalog.service.scanners.models.Codebase;
import com.moneysupermarket.componentcatalog.service.scanners.models.Output;
import com.moneysupermarket.componentcatalog.service.spring.stereotypes.Scanner;
import com.moneysupermarket.componentcatalog.service.utils.FileUtils;
import com.moneysupermarket.componentcatalog.service.utils.ObjectReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Scanner
@RequiredArgsConstructor
@Slf4j
public class LinesOfCodeScanner extends CodebaseScanner {

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
    public Output<Void> scan(Codebase input) {
        LinesOfCode linesOfCode = getLinesOfCode(input);
        return Output.of(component -> component.withLinesOfCode(linesOfCode));
    }

    private LinesOfCode getLinesOfCode(Codebase codebase) {
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

        return new LinesOfCode(count.get(), fileExtensionCounts);
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
