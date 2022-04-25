package tech.kronicle.plugins.nodejs;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.CodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.nodejs.internal.constants.NodeJsFileNames;
import tech.kronicle.plugins.nodejs.internal.services.npm.NpmPackageExtractor;
import tech.kronicle.utils.FileUtils;
import tech.kronicle.sdk.models.Software;
import tech.kronicle.sdk.models.nodejs.NodeJs;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableList;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
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
                .collect(toUnmodifiableList());
        List<Software> software = npmLockFiles.stream()
                .flatMap(file -> npmPackageExtractor.extractPackages(id(), file))
                .collect(toUnmodifiableList());
        return Output.of(component -> component
                .withNodeJs(new NodeJs(!npmLockFiles.isEmpty()))
                .withSoftware(software));
    }

}
