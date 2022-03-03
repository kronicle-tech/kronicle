package tech.kronicle.plugins.todo.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.todo.ToDoScanner;

import static org.assertj.core.api.Assertions.assertThat;

public class GuiceModuleTest {

    private final GuiceModule underTest = new GuiceModule();

    @Test
    public void shouldCreateAToDoScannerInstance() {
        // Given
        Injector guiceInjector = Guice.createInjector(underTest);

        // When
        ToDoScanner returnValue = guiceInjector.getInstance(ToDoScanner.class);

        // Then
        assertThat(returnValue).isNotNull();
    }
}