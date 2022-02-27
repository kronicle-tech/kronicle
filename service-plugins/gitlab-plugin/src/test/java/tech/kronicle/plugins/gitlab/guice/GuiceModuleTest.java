package tech.kronicle.plugins.gitlab.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.gitlab.GitLabRepoFinder;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAGitLabRepoFinderInstance() {
        // Given
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(GitLabConfig.class).toInstance(new GitLabConfig(null, 100, Duration.ofSeconds(60)));
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);

        // When
        GitLabRepoFinder returnValue = guiceInjector.getInstance(GitLabRepoFinder.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}