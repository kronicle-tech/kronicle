package tech.kronicle.service.scanners.readme.services;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadmeFileNameCheckerTest {

    private ReadmeFileNameChecker underTest = new ReadmeFileNameChecker();

    @ParameterizedTest
    @ValueSource(strings = {"README.md", "README.anything", "Readme.md", "Readme.anything", "readme.md", "readme.anything"})
    public void fileNameIsReadmeFileNameShouldReturnTrueForValidReadmeFileNames(String fileName) {
        // Given
        Path file = createTestPathForFileName(fileName);

        // When
        boolean returnValue = underTest.fileNameIsReadmeFileName(file);

        // Then
        assertThat(returnValue).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"README.md1", "README.md%", "1README.md", "%README.md", "test.md"})
    public void fileNameIsReadmeFileNameShouldReturnFalseForInvalidReadmeFileNames(String fileName) {
        // Given
        Path file = createTestPathForFileName(fileName);

        // When
        boolean returnValue = underTest.fileNameIsReadmeFileName(file);

        // Then
        assertThat(returnValue).isFalse();
    }

    private Path createTestPathForFileName(String fileName) {
        return Path.of("fake").resolve(fileName);
    }
}
