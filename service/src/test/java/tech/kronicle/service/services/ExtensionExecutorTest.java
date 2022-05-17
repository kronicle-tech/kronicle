package tech.kronicle.service.services;

import ch.qos.logback.classic.Level;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.finders.Finder;
import tech.kronicle.pluginapi.scanners.Scanner;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.testutils.LogCaptor;
import tech.kronicle.testutils.SimplifiedLogEvent;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import java.time.Duration;
import java.util.List;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtensionExecutorTest {

    public static final Component INPUT = Component.builder().id("test-input").build();
    public static final Output<Integer, Void> FINDER_OUTPUT = createFinderOutput(1);
    public static final Output<Integer, Component> SCANNER_OUTPUT = createScannerOutput(1);

    private final FakeFinder finder = new FakeFinder(1, false);
    private final FakeScanner scanner = new FakeScanner(1, false);
    private LogCaptor logCaptor;

    @BeforeEach
    public void beforeEach() {
        logCaptor = new LogCaptor(ExtensionExecutor.class);
    }

    @AfterEach
    public void afterEach() {
        logCaptor.close();
    }


    @Test
    public void executeFinderShouldCallTheOutputLoaderTheFirstTimeItIsCalledForAParticularKey() {
        // Given
        ExtensionExecutor underTest = createUnderTest();

        // When
        Output<Integer, Void> returnValue = underTest.executeFinder(finder, null, INPUT);

        // Then
        assertThat(returnValue).isEqualTo(FINDER_OUTPUT);
        assertThat(finder.callCount).isEqualTo(1);
        List<SimplifiedLogEvent> simplifiedEvents = logCaptor.getSimplifiedEvents();
        assertThat(simplifiedEvents).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Executing finder fake")
        );
    }

    @Test
    public void executeFinderShouldCallTheOutputLoaderOnlyOnceForAParticularKey() {
        // Given
        ExtensionExecutor underTest = createUnderTest();

        // When
        Output<Integer, Void> returnValue = underTest.executeFinder(finder, null, INPUT);

        // Then
        assertThat(returnValue).isEqualTo(FINDER_OUTPUT);
        assertThat(finder.callCount).isEqualTo(1);

        // When
        returnValue = underTest.executeFinder(finder, null, INPUT);

        // Then
        assertThat(returnValue).isEqualTo(FINDER_OUTPUT);
        assertThat(finder.callCount).isEqualTo(1);
        List<SimplifiedLogEvent> simplifiedEvents = logCaptor.getSimplifiedEvents();
        assertThat(simplifiedEvents).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Executing finder fake")
        );
    }

    @Test
    public void executeFinderShouldIncludeAnErrorInOutputWhenFinderThrowsAnException() {
        // Given
        FakeFinder failingFinder = new FakeFinder(1, true);
        ExtensionExecutor underTest = createUnderTest();

        // When
        Output<Integer, Void> returnValue = underTest.executeFinder(failingFinder, null, INPUT);

        // Then
        assertThat(returnValue).isEqualTo(new Output<>(
                null,
                null,
                List.of(new ScannerError(
                        "fake",
                        "Failed to execute finder",
                        new ScannerError(
                                "fake",
                                "Finder Exception",
                                null
                        )
                )),
                failingFinder.errorCacheTtl()
        ));
        assertThat(failingFinder.callCount).isEqualTo(1);
        List<SimplifiedLogEvent> simplifiedEvents = logCaptor.getSimplifiedEvents();
        assertThat(simplifiedEvents).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Executing finder fake"),
                new SimplifiedLogEvent(Level.ERROR, "Failed to execute finder fake")
        );
    }

    @Test
    public void executeScannerShouldCallTheOutputLoaderTheFirstTimeItIsCalledForAParticularKey() {
        // Given
        ExtensionExecutor underTest = createUnderTest();

        // When
        Output<Integer, Component> returnValue = underTest.executeScanner(scanner, INPUT.reference(), INPUT);

        // Then
        assertThat(returnValue).isEqualTo(SCANNER_OUTPUT);
        assertThat(scanner.callCount).isEqualTo(1);
        List<SimplifiedLogEvent> simplifiedEvents = logCaptor.getSimplifiedEvents();
        assertThat(simplifiedEvents).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Executing scanner fake for \"test-input\"")
        );
    }

    @Test
    public void executeScannerShouldCallTheOutputLoaderOnlyOnceForAParticularKey() {
        // Given
        ExtensionExecutor underTest = createUnderTest();

        // When
        Output<Integer, Component> returnValue = underTest.executeScanner(scanner, INPUT.reference(), INPUT);

        // Then
        assertThat(returnValue).isEqualTo(SCANNER_OUTPUT);
        assertThat(scanner.callCount).isEqualTo(1);

        // When
        returnValue = underTest.executeScanner(scanner, INPUT.reference(), INPUT);

        // Then
        assertThat(returnValue).isEqualTo(SCANNER_OUTPUT);
        assertThat(scanner.callCount).isEqualTo(1);
        List<SimplifiedLogEvent> simplifiedEvents = logCaptor.getSimplifiedEvents();
        assertThat(simplifiedEvents).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Executing scanner fake for \"test-input\"")
        );
    }

    @Test
    public void executeScannerShouldIncludeAnErrorInOutputWhenScannerThrowsAnException() {
        // Given
        FakeScanner failingScanner = new FakeScanner(1, true);
        ExtensionExecutor underTest = createUnderTest();

        // When
        Output<Integer, Component> returnValue = underTest.executeScanner(failingScanner, INPUT.reference(), INPUT);

        // Then
        assertThat(returnValue).isEqualTo(new Output<>(
                null,
                null,
                List.of(new ScannerError(
                        "fake",
                        "Failed to scan \"test-input\"",
                        new ScannerError(
                                "fake",
                                "Scanner Exception",
                                null
                        )
                )),
                failingScanner.errorCacheTtl()
        ));
        assertThat(failingScanner.callCount).isEqualTo(1);
        List<SimplifiedLogEvent> simplifiedEvents = logCaptor.getSimplifiedEvents();
        assertThat(simplifiedEvents).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Executing scanner fake for \"test-input\""),
                new SimplifiedLogEvent(Level.ERROR, "Scanner fake failed to scan \"test-input\"")
        );
    }

    private ExtensionExecutor createUnderTest() {
        return new ExtensionExecutor(
                new ExtensionOutputCache(
                    new ExtensionOutputCacheLoader(),
                    new ExtensionOutputCacheExpiry()
                ),
                new ThrowableToScannerErrorMapper()
        );
    }

    private static Output<Integer, Void> createFinderOutput(int outputNumber) {
        return new Output<>(outputNumber, null, List.of(), Duration.ofMinutes(outputNumber));
    }

    private static Output<Integer, Component> createScannerOutput(int outputNumber) {
        return new Output<>(outputNumber, UnaryOperator.identity(), List.of(), Duration.ofMinutes(outputNumber));
    }

    @RequiredArgsConstructor
    private static class FakeFinder extends Finder<Component, Integer> {

        private final int outputNumber;
        private final boolean throwException;
        private int callCount;

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Integer, Void> find(Component input) {
            assertThat(input).isEqualTo(INPUT);
            callCount++;
            if (throwException) {
                throw new RuntimeException("Finder Exception");
            } else {
                return new Output<>(outputNumber, null, List.of(), Duration.ofMinutes(outputNumber));
            }
        }
    }

    @RequiredArgsConstructor
    private static class FakeScanner extends Scanner<Component, Integer> {

        private final int outputNumber;
        private final boolean throwException;
        private int callCount;

        @Override
        public String description() {
            return null;
        }

        @Override
        public Output<Integer, Component> scan(Component input) {
            assertThat(input).isEqualTo(INPUT);
            callCount++;
            if (throwException) {
                throw new RuntimeException("Scanner Exception");
            } else {
                return new Output<>(outputNumber, UnaryOperator.identity(), List.of(), Duration.ofMinutes(outputNumber));
            }
        }
    }
}
