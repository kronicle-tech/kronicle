package tech.kronicle.plugins.doc.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.doc.DocScanner;
import tech.kronicle.plugins.doc.config.DocConfig;
import tech.kronicle.plugins.doc.guice.GuiceModule;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAnExampleScanner() {
        // Given
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(DocConfig.class).toInstance(new DocConfig());
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);

        // When
        DocScanner returnValue = guiceInjector.getInstance(DocScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}