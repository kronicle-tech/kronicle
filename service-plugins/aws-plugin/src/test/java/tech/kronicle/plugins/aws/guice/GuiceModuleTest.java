package tech.kronicle.plugins.aws.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.aws.AwsComponentFinder;
import tech.kronicle.plugins.aws.AwsXrayTracingDataFinder;
import tech.kronicle.plugins.aws.config.AwsConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAnAwsComponentFinder() {
        // Given
        Injector guiceInjector = createGuiceInjector();

        // When
        AwsComponentFinder returnValue = guiceInjector.getInstance(AwsComponentFinder.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    @Test
    public void shouldCreateAnAwsXrayDependencyFinder() {
        // Given
        Injector guiceInjector = createGuiceInjector();

        // When
        AwsXrayTracingDataFinder returnValue = guiceInjector.getInstance(AwsXrayTracingDataFinder.class);

        // Then
        assertThat(returnValue).isNotNull();
    }

    private Injector createGuiceInjector() {
        AbstractModule configModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(AwsConfig.class).toInstance(new AwsConfig(null, null, null, null));
            }
        };
        Injector guiceInjector = Guice.createInjector(underTest, configModule);
        return guiceInjector;
    }
}