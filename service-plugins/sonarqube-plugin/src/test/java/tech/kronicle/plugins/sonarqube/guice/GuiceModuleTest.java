package tech.kronicle.plugins.sonarqube.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.sonarqube.SonarQubeScanner;
import tech.kronicle.plugins.sonarqube.config.SonarQubeConfig;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateASonarQubeScannerInstance() {
        // Given
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(SonarQubeConfig.class).toInstance(new SonarQubeConfig(null, Duration.ofSeconds(60), null));
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);

        // When
        SonarQubeScanner returnValue = guiceInjector.getInstance(SonarQubeScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}