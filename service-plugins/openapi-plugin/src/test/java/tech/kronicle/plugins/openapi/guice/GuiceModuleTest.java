package tech.kronicle.plugins.openapi.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.openapi.OpenApiScanner;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAOpenApiScannerInstance() {
        // Given
        Injector guiceInjector = Guice.createInjector(underTest);

        // When
        OpenApiScanner returnValue = guiceInjector.getInstance(OpenApiScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}