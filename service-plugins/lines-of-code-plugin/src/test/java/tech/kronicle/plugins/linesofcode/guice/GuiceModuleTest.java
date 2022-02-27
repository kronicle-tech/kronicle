package tech.kronicle.plugins.linesofcode.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.linesofcode.LinesOfCodeScanner;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateALinesOfCodeScannerInstance() {
        // Given
        Injector guiceInjector = Guice.createInjector(underTest);

        // When
        LinesOfCodeScanner returnValue = guiceInjector.getInstance(LinesOfCodeScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}