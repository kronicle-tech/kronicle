package tech.kronicle.plugins.repostate.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.repostate.RepoStateScanner;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAnExampleScanner() {
        // Given
        Injector guiceInjector = Guice.createInjector(underTest);

        // When
        RepoStateScanner returnValue = guiceInjector.getInstance(RepoStateScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}