package tech.kronicle.service.scanners.nodejs;

import lombok.RequiredArgsConstructor;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.nodejs.NodeJs;
import tech.kronicle.service.scanners.CodebaseScanner;
import tech.kronicle.service.scanners.models.Codebase;
import tech.kronicle.service.scanners.models.Output;
import tech.kronicle.service.scanners.nodejs.internal.constants.NodeJsFileNames;
import tech.kronicle.service.scanners.nodejs.internal.services.npm.NpmPackageExtractor;
import tech.kronicle.service.spring.stereotypes.Scanner;
import tech.kronicle.service.utils.FileUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Scanner
@RequiredArgsConstructor
public class NodeJsScanner  extends CodebaseScanner {

    private final FileUtils fileUtils;
    private final NpmPackageExtractor npmPackageExtractor;

    @Override
    public String id() {
        return "nodejs";
    }

    @Override
    public String description() {
        return "Scans a component's codebase for any node.js package-lock.json or yarn.lock files to find what software is used";
    }

    @Override
    public String notes() {
        return "If the scanner finds node.js package-lock.json or yarn.lock files, it will:\n"
                + "\n"
                + "* Find the names and versions of any npm packages used";
    }

    @Override
    public Output<Void> scan(Codebase input) {
        List<Path> npmLockFiles = fileUtils.findFiles(input.getDir(), (file, ignored) -> file.endsWith(NodeJsFileNames.NPM_PACKAGE_LOCK_JSON))
                .collect(Collectors.toList());
        List<Software> software = npmLockFiles.stream()
                .flatMap(file -> npmPackageExtractor.extractPackages(id(), file))
                .collect(Collectors.toList());
        return Output.of(component -> component
                .withNodeJs(new NodeJs(!npmLockFiles.isEmpty()))
                .withSoftware(software));
    }

}
