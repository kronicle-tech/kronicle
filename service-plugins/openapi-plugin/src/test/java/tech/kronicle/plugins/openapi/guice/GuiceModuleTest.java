package tech.kronicle.plugins.openapi.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.openapi.OpenApiScanner;
import tech.kronicle.plugins.openapi.config.OpenApiConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAOpenApiScannerInstance() {
        // Given
        Injector guiceInjector = createGuiceInjector();

        // When
        OpenApiScanner returnValue = guiceInjector.getInstance(OpenApiScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    private Injector createGuiceInjector() {
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(OpenApiConfig.class).toInstance(new OpenApiConfig(
                        null
                ));
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);
        return guiceInjector;
    }
}