package tech.kronicle.plugins.doc.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.doc.models.FileType;
import tech.kronicle.sdk.models.doc.DocFileContentType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileTypeRegistryTest {

    @Test
    public void getFileTypeShouldReturnAFileTypeWhenThereIsAMatch() {
        // Given
        FileTypeRegistry underTest = new FileTypeRegistry();

        // When
        FileType returnValue = underTest.getFileType("example-2.md");

        // Then
        assertThat(returnValue).isEqualTo(
                new FileType(List.of("md", "markdown"), "text/markdown", DocFileContentType.Text)
        );
    }

    @Test
    public void getFileTypeShouldReturnNullWhenThereIsNoMatch() {
        // Given
        FileTypeRegistry underTest = new FileTypeRegistry();

        // When
        FileType returnValue = underTest.getFileType("example.does-not-exist");

        // Then
        assertThat(returnValue).isNull();
    }
}
