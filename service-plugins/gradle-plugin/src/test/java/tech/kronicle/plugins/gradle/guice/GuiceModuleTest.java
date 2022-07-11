package tech.kronicle.plugins.gradle.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.gradle.GradleScanner;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAGradleScannerInstance() {
        // Given
        Injector guiceInjector = Guice.createInjector(underTest);

        // When
        GradleScanner returnValue = guiceInjector.getInstance(GradleScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}