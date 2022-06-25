package tech.kronicle.plugins.doc.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.doc.DocScanner;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAnExampleScanner() {
        // Given
        Injector guiceInjector = Guice.createInjector(underTest);

        // When
        DocScanner returnValue = guiceInjector.getInstance(DocScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}