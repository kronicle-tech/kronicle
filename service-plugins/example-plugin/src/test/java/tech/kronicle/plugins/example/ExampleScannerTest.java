package tech.kronicle.plugins.example;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ExampleScannerTest {

    @Test
    public void descriptionShouldThrowAnIllegalStateException() {
        // Given
        ExampleScanner underTest = new ExampleScanner();

        // When
        Throwable thrown = catchThrowable(() -> underTest.description());

        // Then
        assertThat(thrown).isInstanceOf(IllegalStateException.class);
        assertThat(thrown).hasMessage("Not implemented");
    }

    @Test
    public void scanShouldThrowAnIllegalStateException() {
        // Given
        ExampleScanner underTest = new ExampleScanner();

        // When
        Throwable thrown = catchThrowable(() -> underTest.scan(Component.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(IllegalStateException.class);
        assertThat(thrown).hasMessage("Not implemented");
    }
}
