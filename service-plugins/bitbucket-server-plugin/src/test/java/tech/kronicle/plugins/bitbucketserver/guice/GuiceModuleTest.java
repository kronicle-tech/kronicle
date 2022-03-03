package tech.kronicle.plugins.bitbucketserver.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.bitbucketserver.BitbucketServerRepoFinder;
import tech.kronicle.plugins.bitbucketserver.config.BitbucketServerConfig;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateABitbucketServerRepoFinderInstance() {
        // Given
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(BitbucketServerConfig.class).toInstance(new BitbucketServerConfig(
                        null,
                        Duration.ofSeconds(60)
                ));
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);

        // When
        BitbucketServerRepoFinder returnValue = guiceInjector.getInstance(BitbucketServerRepoFinder.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}