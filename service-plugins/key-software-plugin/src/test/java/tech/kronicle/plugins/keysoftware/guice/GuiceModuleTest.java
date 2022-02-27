package tech.kronicle.plugins.keysoftware.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.keysoftware.KeySoftwareScanner;
import tech.kronicle.plugins.keysoftware.config.KeySoftwareConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAKeySoftwareScannerInstance() {
        // Given
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(KeySoftwareConfig.class).toInstance(new KeySoftwareConfig(true, List.of(), null));
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);

        // When
        KeySoftwareScanner returnValue = guiceInjector.getInstance(KeySoftwareScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}