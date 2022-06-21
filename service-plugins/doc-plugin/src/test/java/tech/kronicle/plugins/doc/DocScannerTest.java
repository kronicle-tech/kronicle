package tech.kronicle.plugins.doc;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class DocScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // When
        DocScanner underTest = new DocScanner();
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("doc");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // When
        DocScanner underTest = new DocScanner();
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Saves documentation files from a component's Git repo and serves those files via Kronicle's UI");
    }

    @Test
    public void scanShouldReturnAnEmptyOutput() {
        // Given
        DocScanner underTest = new DocScanner();

        // When
        Output<Void, Component> returnValue = underTest.scan(Component.builder().build());

        // Then
        assertThat(returnValue).isEqualTo(Output.empty(CACHE_TTL));
    }
}
