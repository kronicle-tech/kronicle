package tech.kronicle.pluginapi.finders.models;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Diagram;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ComponentsAndDiagramsTest {

    @Test
    public void constructorShouldMakeComponentsAnUnmodifiableMap() {
        // Given
        ComponentsAndDiagrams underTest = ComponentsAndDiagrams.builder().components(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getComponents().add(Component.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeDiagramsAnUnmodifiableMap() {
        // Given
        ComponentsAndDiagrams underTest = ComponentsAndDiagrams.builder().diagrams(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getDiagrams().add(Diagram.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
