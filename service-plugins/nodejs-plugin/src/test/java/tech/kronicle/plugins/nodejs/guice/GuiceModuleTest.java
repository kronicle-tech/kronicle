package tech.kronicle.plugins.nodejs.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.nodejs.NodeJsScanner;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateANodeJsScannerInstance() {
        // Given
        Injector guiceInjector = Guice.createInjector(underTest);

        // When
        NodeJsScanner returnValue = guiceInjector.getInstance(NodeJsScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}