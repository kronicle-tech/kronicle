package tech.kronicle.service.services;

import org.pf4j.PluginManager;
import org.springframework.stereotype.Service;
import tech.kronicle.service.scanners.CodebaseScanner;
import tech.kronicle.service.scanners.ComponentAndCodebaseScanner;
import tech.kronicle.service.scanners.ComponentScanner;
import tech.kronicle.service.scanners.LateComponentScanner;
import tech.kronicle.service.scanners.RepoScanner;
import tech.kronicle.service.scanners.Scanner;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScannerRegistry extends BaseRegistry<Scanner<?, ?>> {

    public ScannerRegistry(PluginManager pluginManager) {
        super(getScanners(pluginManager));
    }

    private static List<Scanner<?, ?>> getScanners(PluginManager pluginManager) {
        return pluginManager.getExtensions(Scanner.class).stream()
                .map(it -> (Scanner<?, ?>) it)
                .collect(Collectors.toList());
    }

    public RepoScanner getRepoScanner() {
        List<RepoScanner> scanners = getItems(RepoScanner.class);

        if (scanners.isEmpty()) {
            throw new RuntimeException("No RepoScanner has been configured");
        } else if (scanners.size() > 1) {
            throw new RuntimeException("More than 1 RepoScanner has been configured");
        } else {
            return scanners.get(0);
        }
    }

    public List<ComponentScanner> getComponentScanners() {
        return getItems(ComponentScanner.class);
    }

    public List<CodebaseScanner> getCodebaseScanners() {
        return getItems(CodebaseScanner.class);
    }

    public List<ComponentAndCodebaseScanner> getComponentAndCodebaseScanners() {
        return getItems(ComponentAndCodebaseScanner.class);
    }

    public List<LateComponentScanner> getLateComponentScanners() {
        return getItems(LateComponentScanner.class);
    }
}
