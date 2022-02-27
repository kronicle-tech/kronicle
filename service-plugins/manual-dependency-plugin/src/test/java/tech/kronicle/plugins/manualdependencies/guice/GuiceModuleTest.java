package tech.kronicle.plugins.manualdependencies.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.manualdependencies.ManualDependencyFinder;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAManualDependencyFinderInstance() {
        // Given
        Injector guiceInjector = Guice.createInjector(underTest);

        // When
        ManualDependencyFinder returnValue = guiceInjector.getInstance(ManualDependencyFinder.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}