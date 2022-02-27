package tech.kronicle.plugins.gradle.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.gradle.GradleScanner;
import tech.kronicle.plugins.gradle.config.DownloadCacheConfig;
import tech.kronicle.plugins.gradle.config.DownloaderConfig;
import tech.kronicle.plugins.gradle.config.GradleConfig;
import tech.kronicle.plugins.gradle.config.PomCacheConfig;
import tech.kronicle.plugins.gradle.config.UrlExistsCacheConfig;

import java.nio.file.Files;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAGradleScannerInstance() {
        // Given
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(GradleConfig.class).toInstance(new GradleConfig(
                        null,
                        null,
                        new DownloaderConfig(Duration.ofSeconds(60)),
                        new DownloadCacheConfig(createTempDirectory()),
                        new UrlExistsCacheConfig(createTempDirectory()),
                        new PomCacheConfig(createTempDirectory())
                ));
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);

        // When
        GradleScanner returnValue = guiceInjector.getInstance(GradleScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @SneakyThrows
    private String createTempDirectory() {
        return Files.createTempDirectory(this.getClass().getName()).toString();
    }
}