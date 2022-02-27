package tech.kronicle.plugins.git.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.git.GitScanner;
import tech.kronicle.plugins.git.config.GitConfig;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAGitScannerInstance() {
        // Given
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(GitConfig.class).toInstance(new GitConfig(createTempDirectory(), null));
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);

        // When
        GitScanner returnValue = guiceInjector.getInstance(GitScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @SneakyThrows
    private String createTempDirectory() {
        return Files.createTempDirectory(this.getClass().getName()).toString();
    }
}