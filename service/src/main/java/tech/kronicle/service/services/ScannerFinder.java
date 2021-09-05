package tech.kronicle.service.services;

import tech.kronicle.service.scanners.CodebaseScanner;
import tech.kronicle.service.scanners.ComponentAndCodebaseScanner;
import tech.kronicle.service.scanners.ComponentScanner;
import tech.kronicle.service.scanners.LateComponentScanner;
import tech.kronicle.service.scanners.RepoScanner;
import tech.kronicle.service.scanners.Scanner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScannerFinder {

    private final List<Scanner<?, ?>> scanners;

    public RepoScanner getRepoScanner() {
        List<RepoScanner> scanners = getScanners(RepoScanner.class);

        if (scanners.isEmpty()) {
            throw new RuntimeException("No RepoScanner has been configured");
        } else if (scanners.size() > 1) {
            throw new RuntimeException("More than 1 RepoScanner has been configured");
        } else {
            return scanners.get(0);
        }
    }

    public List<ComponentScanner> getComponentScanners() {
        return getScanners(ComponentScanner.class);
    }

    public List<CodebaseScanner> getCodebaseScanners() {
        return getScanners(CodebaseScanner.class);
    }

    public List<ComponentAndCodebaseScanner> getComponentAndCodebaseScanners() {
        return getScanners(ComponentAndCodebaseScanner.class);
    }

    public List<LateComponentScanner> getLateComponentScanners() {
        return getScanners(LateComponentScanner.class);
    }

    public List<Scanner<?, ?>> getAllScanners() {
        return List.copyOf(scanners);
    }

    public Scanner<?, ?> getScanner(String scannerId) {
        return scanners.stream()
                .filter(scanner -> Objects.equals(scanner.id(), scannerId))
                .findFirst().orElse(null);
    }

    private <T extends Scanner<?, ?>> List<T> getScanners(Class<T> clazz) {
        return scanners.stream()
                .filter(scanner -> clazz.isAssignableFrom(scanner.getClass()))
                .map(scanner -> (T) scanner)
                .collect(Collectors.toList());
    }
}
