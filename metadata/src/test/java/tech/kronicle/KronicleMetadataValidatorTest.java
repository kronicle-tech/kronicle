package tech.kronicle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class KronicleMetadataValidatorTest {

    @TempDir
    public Path tempDir;

    @Test
    public void validateShouldReturnWhenFileIsValid() throws IOException {
        // Given
        Path file = tempDir.resolve("valid.yaml");
        Files.writeString(file, ""
                + "components:\n"
                + "  - id: test-component-id\n"
                + "    name: Test Component Name\n"
                + "    type: service\n"
                + "    repo:\n"
                + "      url: https://example.com/repo\n");

        // When
        KronicleMetadataValidator.validate(file.toFile());

        // Then
        // No exception has been raised
    }

    @Test
    public void validateShouldThrowAnExceptionWhenFileIsInvalid() throws IOException {
        // Given
        Path file = tempDir.resolve("valid.yaml");
        Files.writeString(file, ""
                + "components:\n"
                + "  - id: test-component-id\n");

        // When
        Throwable thrown = catchThrowable(() -> KronicleMetadataValidator.validate(file.toFile()));

        // Then
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown).hasMessage("Component Metadata file has failed validation:\n"
                + "- components[0].name with value \"null\" must not be blank\n"
                + "- components[0].type with value \"null\" must not be blank");
    }
}
