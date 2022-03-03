package tech.kronicle.plugins.readme.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.readme.ReadmeScanner;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAReadmeScannerInstance() {
        // Given
        Injector guiceInjector = Guice.createInjector(underTest);

        // When
        ReadmeScanner returnValue = guiceInjector.getInstance(ReadmeScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}