package tech.kronicle.plugins.zipkin.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.zipkin.ZipkinTracingDataFinder;
import tech.kronicle.plugins.zipkin.config.ZipkinConfig;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAZipkinScannerInstance() {
        // Given
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ZipkinConfig.class).toInstance(new ZipkinConfig(
                        null,
                        null,
                        Duration.ofSeconds(60),
                        null,
                        null,
                        null
                ));
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);

        // When
        ZipkinTracingDataFinder returnValue = guiceInjector.getInstance(ZipkinTracingDataFinder.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}