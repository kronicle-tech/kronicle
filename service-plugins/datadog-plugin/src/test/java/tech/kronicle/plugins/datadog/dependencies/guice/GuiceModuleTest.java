package tech.kronicle.plugins.datadog.dependencies.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.datadog.DatadogDependencyFinder;
import tech.kronicle.plugins.datadog.config.DatadogConfig;
import tech.kronicle.plugins.datadog.dependencies.config.DatadogDependenciesConfig;
import tech.kronicle.plugins.datadog.guice.GuiceModule;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateADatadogDependencyFinderInstance() {
        // Given
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(DatadogConfig.class).toInstance(new DatadogConfig(
                        null,
                        Duration.ofSeconds(60),
                        null,
                        null,
                        new DatadogDependenciesConfig(null)
                ));
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);

        // When
        DatadogDependencyFinder returnValue = guiceInjector.getInstance(DatadogDependencyFinder.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}