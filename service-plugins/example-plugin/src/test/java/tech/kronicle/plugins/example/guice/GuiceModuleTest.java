package tech.kronicle.plugins.example.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.example.ExampleScanner;
import tech.kronicle.plugins.example.config.ExampleConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAnExampleScanner() {
        // Given
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ExampleConfig.class).toInstance(new ExampleConfig());
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);

        // When
        ExampleScanner returnValue = guiceInjector.getInstance(ExampleScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}