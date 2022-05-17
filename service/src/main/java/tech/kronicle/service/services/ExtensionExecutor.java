package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.common.StringEscapeUtils;
import tech.kronicle.pluginapi.finders.Finder;
import tech.kronicle.pluginapi.scanners.Scanner;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ObjectWithReference;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExtensionExecutor {

    private final ExtensionOutputCache cache;
    private final ThrowableToScannerErrorMapper throwableToScannerErrorMapper;

    public <K, I, O> Output<O, Void> executeFinder(
            Finder<I, O> finder,
            K key,
            I input) {
        return cache.get(
                finder,
                key,
                input,
                () -> loadFromFinder(finder, input)
        );
    }

    public <K, I extends ObjectWithReference, O> Output<O, Component> executeScanner(
            Scanner<I, O> scanner,
            K key,
            I input
    ) {
        return cache.get(
                scanner,
                key,
                input,
                () -> loadFromScanner(scanner, input)
        );
    }

    private <I, O> Output<O, Void> loadFromFinder(Finder<I, O> finder, I input) {
        log.info("Executing finder {}", finder.id());
        try {
            return finder.find(input);
        } catch (Exception e) {
            log.error("Failed to execute finder {}", finder.id(), e);
            return Output.ofError(
                    new ScannerError(
                            finder.id(),
                            "Failed to execute finder",
                            throwableToScannerErrorMapper.map(finder.id(), e)
                    ),
                    finder.errorCacheTtl()
            );
        }
    }

    private <I extends ObjectWithReference, O> Output<O, Component> loadFromScanner(Scanner<I, O> scanner, I input) {
        log.info("Executing scanner {} for \"{}\"", scanner.id(), StringEscapeUtils.escapeString(input.reference()));
        try {
            return scanner.scan(input);
        } catch (Exception e) {
            log.error("Scanner {} failed to scan \"{}\"", scanner.id(), input.reference(), e);
            return Output.ofError(
                    new ScannerError(
                            scanner.id(),
                            String.format(
                                    "Failed to scan \"%s\"",
                                    StringEscapeUtils.escapeString(input.reference())
                            ),
                            throwableToScannerErrorMapper.map(scanner.id(), e)
                    ),
                    scanner.errorCacheTtl()
            );
        }
    }
}
