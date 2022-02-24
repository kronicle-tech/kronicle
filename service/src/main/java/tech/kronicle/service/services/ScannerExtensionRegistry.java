package tech.kronicle.service.services;

import org.pf4j.PluginManager;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.scanners.CodebaseScanner;
import tech.kronicle.pluginapi.scanners.ComponentAndCodebaseScanner;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.LateComponentScanner;
import tech.kronicle.pluginapi.scanners.RepoScanner;
import tech.kronicle.pluginapi.scanners.Scanner;

import java.util.List;

@Service
public class ScannerExtensionRegistry extends BaseExtensionRegistry<Scanner> {

    public ScannerExtensionRegistry(PluginManager pluginManager) {
        super(pluginManager);
    }

    @Override
    protected Class<Scanner> getItemType() {
        return Scanner.class;
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
