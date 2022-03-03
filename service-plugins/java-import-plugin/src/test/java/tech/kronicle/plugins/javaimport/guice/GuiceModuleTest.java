package tech.kronicle.plugins.javaimport.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.javaimport.JavaImportScanner;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAJavaImportScannerInstance() {
        // Given
        Injector guiceInjector = Guice.createInjector(underTest);

        // When
        JavaImportScanner returnValue = guiceInjector.getInstance(JavaImportScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}