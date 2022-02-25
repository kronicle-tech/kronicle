package tech.kronicle.pluginapi.scanners.models;

import tech.kronicle.sdk.models.Repo;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class CodebaseTest {

    @Test
    public void referenceShouldReturnReferenceOfRepo() {
        // Given
        Codebase underTest = new Codebase(new Repo("https://example.com"), Path.of("test"));

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("https://example.com");
    }
}
