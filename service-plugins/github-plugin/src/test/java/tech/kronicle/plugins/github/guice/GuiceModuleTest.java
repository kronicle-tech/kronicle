package tech.kronicle.plugins.github.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.github.GitHubRepoFinder;
import tech.kronicle.plugins.github.config.GitHubConfig;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAGitHubRepoFinderInstance() {
        // Given
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(GitHubConfig.class).toInstance(new GitHubConfig(null, null, null, null, Duration.ofSeconds(60)));
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);

        // When
        GitHubRepoFinder returnValue = guiceInjector.getInstance(GitHubRepoFinder.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}