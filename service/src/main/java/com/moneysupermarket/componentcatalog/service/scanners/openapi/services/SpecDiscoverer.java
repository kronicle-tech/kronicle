package com.moneysupermarket.componentcatalog.service.scanners.openapi.services;

import com.moneysupermarket.componentcatalog.sdk.models.openapi.OpenApiSpec;
import com.moneysupermarket.componentcatalog.service.scanners.Scanner;
import com.moneysupermarket.componentcatalog.service.scanners.models.ComponentAndCodebase;
import com.moneysupermarket.componentcatalog.service.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class SpecDiscoverer {

    private static final List<String> OPENAPI_SPEC_FILE_EXTENSIONS = List.of("yaml", "yml", "json");

    private final FileUtils fileUtils;

    public void discoverSpecsInCodebase(Scanner scanner, ComponentAndCodebase input, List<OpenApiSpec> specs) {
        fileUtils.findFiles(input.getCodebase().getDir(), this::isPossibleOpenApiSpecFile)
                .map(getCodebaseRelativeFile(input))
                .filter(doesSpecNotAlreadyExistForFile(specs))
                .map(createSpecFromFile(scanner))
                .forEach(specs::add);
    }

    private boolean isPossibleOpenApiSpecFile(Path path, BasicFileAttributes attributes) {
        return OPENAPI_SPEC_FILE_EXTENSIONS.contains(getFileExtension(path));
    }

    private String getFileExtension(Path path) {
        return FilenameUtils.getExtension(path.getFileName().toString());
    }

    private Function<Path, String> getCodebaseRelativeFile(ComponentAndCodebase input) {
        return file -> input.getCodebase().getDir().relativize(file).toString();
    }

    private Predicate<String> doesSpecNotAlreadyExistForFile(List<OpenApiSpec> specs) {
        return file -> specs.stream().noneMatch(spec -> Objects.equals(spec.getFile(), file));
    }

    private Function<String, OpenApiSpec> createSpecFromFile(Scanner scanner) {
        return file -> new OpenApiSpec(scanner.id(), null, file, null, null);
    }
}
