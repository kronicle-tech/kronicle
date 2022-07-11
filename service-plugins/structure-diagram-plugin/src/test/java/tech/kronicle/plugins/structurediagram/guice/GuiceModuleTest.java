package tech.kronicle.plugins.structurediagram.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.structurediagram.StructureDiagramFinder;
import tech.kronicle.plugins.structurediagram.guice.GuiceModule;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAnExampleScanner() {
        // Given
        Injector guiceInjector = Guice.createInjector(underTest);

        // When
        StructureDiagramFinder returnValue = guiceInjector.getInstance(StructureDiagramFinder.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}