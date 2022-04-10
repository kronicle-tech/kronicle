package tech.kronicle.pluginapi.scanners.models;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.RepoReference;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class CodebaseTest {

    @Test
    public void referenceShouldReturnReferenceOfRepo() {
        // Given
        Codebase underTest = new Codebase(new RepoReference("https://example.com"), Path.of("test"));

        // When
        String returnValue = underTest.reference();

        // Then
        assertThat(returnValue).isEqualTo("https://example.com");
    }
}
