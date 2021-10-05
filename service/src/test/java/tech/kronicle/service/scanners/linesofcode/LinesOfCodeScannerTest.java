package tech.kronicle.service.scanners.linesofcode;

import com.google.common.base.Ascii;
import tech.kronicle.sdk.models.linesofcode.FileExtensionCount;
import tech.kronicle.sdk.models.linesofcode.LinesOfCode;
import tech.kronicle.service.scanners.BaseCodebaseScannerTest;
import tech.kronicle.service.scanners.linesofcode.services.LinesOfCodeCounter;
import tech.kronicle.service.scanners.models.Codebase;
import tech.kronicle.service.scanners.models.Output;
import tech.kronicle.service.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.service.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class LinesOfCodeScannerTest extends BaseCodebaseScannerTest {

    private LinesOfCodeScanner underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new LinesOfCodeScanner(new FileUtils(new AntStyleIgnoreFileLoader()), new LinesOfCodeCounter());
    }

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("lines-of-code");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Scans a component's codebase, finding all the file extensions for textual files in the codebase and calculates the "
                + "total number of lines of text for each of those file extensions");
    }

    @Test
    public void notesShouldReturnNull() {
        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void scanShouldHandleNoFiles() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("NoFiles"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        LinesOfCode linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
        assertThat(linesOfCode).isNotNull();
        // The count includes the single line in the ".gitignore" file
        assertThat(linesOfCode.getCount()).isEqualTo(1);
        assertThat(linesOfCode.getFileExtensionCounts()).hasSize(1);
        assertThat(linesOfCode.getFileExtensionCounts()).containsExactly(new FileExtensionCount("gitignore", 1));
    }

    @Test
    public void scanShouldHandleSingleFileWithTerminatingNewLine() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("SingleFileWithTerminatingNewLine"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        LinesOfCode linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
        assertThat(linesOfCode).isNotNull();
        assertThat(linesOfCode.getCount()).isEqualTo(3);
        assertThat(linesOfCode.getFileExtensionCounts()).hasSize(1);
        assertThat(linesOfCode.getFileExtensionCounts()).containsExactly(new FileExtensionCount("txt", 3));
    }

    @Test
    public void scanShouldHandleSingleFileWithNoTerminatingNewLine() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("SingleFileWithNoTerminatingNewLine"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        LinesOfCode linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
        assertThat(linesOfCode).isNotNull();
        assertThat(linesOfCode.getCount()).isEqualTo(2);
        assertThat(linesOfCode.getFileExtensionCounts()).hasSize(1);
        assertThat(linesOfCode.getFileExtensionCounts()).containsExactly(new FileExtensionCount("txt", 2));
    }

    @Test
    public void scanShouldHandleMultipleFilesWithDifferentFileExtension() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultipleFilesWithDifferentFileExtensions"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        LinesOfCode linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
        assertThat(linesOfCode).isNotNull();
        assertThat(linesOfCode.getCount()).isEqualTo(5);
        assertThat(linesOfCode.getFileExtensionCounts()).hasSize(2);
        assertThat(linesOfCode.getFileExtensionCounts()).containsExactly(new FileExtensionCount("txt", 3),
                new FileExtensionCount("js", 2));
    }

    @Test
    public void scanShouldHandleMultipleFilesWithSameFileExtension() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("MultipleFilesWithSameFileExtension"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        LinesOfCode linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
        assertThat(linesOfCode).isNotNull();
        assertThat(linesOfCode.getCount()).isEqualTo(5);
        assertThat(linesOfCode.getFileExtensionCounts()).hasSize(1);
        assertThat(linesOfCode.getFileExtensionCounts()).containsExactly(new FileExtensionCount("txt", 5));
    }

    @Test
    public void scanShouldHandleSubdirectories() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("Subdirectories"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        LinesOfCode linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
        assertThat(linesOfCode).isNotNull();
        assertThat(linesOfCode.getCount()).isEqualTo(6);
        assertThat(linesOfCode.getFileExtensionCounts()).hasSize(1);
        assertThat(linesOfCode.getFileExtensionCounts()).containsExactly(new FileExtensionCount("txt", 6));
    }

    @Test
    public void scanShouldIgnoreBinaryFile() throws IOException {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("BinaryFile"));
        assertThat(Files.readString(codebase.getDir().resolve("binary_file.bin"))).isEqualTo(Character.valueOf((char) Ascii.VT).toString().repeat(100));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        LinesOfCode linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
        assertThat(linesOfCode).isNotNull();
        assertThat(linesOfCode.getCount()).isEqualTo(1);
        assertThat(linesOfCode.getFileExtensionCounts()).hasSize(2);
        assertThat(linesOfCode.getFileExtensionCounts()).containsExactly(new FileExtensionCount("txt", 1),
                new FileExtensionCount("bin", 0));
    }

    @Test
    public void scanShouldSortHandleMultipleFilesByLinesOfCode() {
        // Given
        Codebase codebase = new Codebase(getTestRepo(), getCodebaseDir("SortMultipleFilesByLinesOfCode"));

        // When
        Output<Void> returnValue = underTest.scan(codebase);

        // Then
        assertThat(returnValue.getErrors()).isEmpty();
        LinesOfCode linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
        assertThat(linesOfCode).isNotNull();
        assertThat(linesOfCode.getCount()).isEqualTo(6);
        assertThat(linesOfCode.getFileExtensionCounts()).hasSize(3);
        assertThat(linesOfCode.getFileExtensionCounts()).containsExactly(new FileExtensionCount("js", 3),
                new FileExtensionCount("java", 2),
                new FileExtensionCount("txt", 1));
    }
}
