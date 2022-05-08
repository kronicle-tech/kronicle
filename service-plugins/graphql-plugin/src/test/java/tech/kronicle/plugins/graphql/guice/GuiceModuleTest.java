package tech.kronicle.plugins.graphql.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.graphql.GraphQlScanner;
import tech.kronicle.plugins.graphql.config.GraphQlConfig;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAGraphQlScannerInstance() {
        // Given
        Injector guiceInjector = createGuiceInjector();

        // When
        GraphQlScanner returnValue = guiceInjector.getInstance(GraphQlScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    private Injector createGuiceInjector() {
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(GraphQlConfig.class).toInstance(new GraphQlConfig(
                        Duration.ofMinutes(1)
                ));
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);
        return guiceInjector;
    }
}