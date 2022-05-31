package tech.kronicle.plugins.linesofcode;

import com.google.common.base.Ascii;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.linesofcode.services.LinesOfCodeCounter;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.linesofcode.FileExtensionCount;
import tech.kronicle.sdk.models.linesofcode.LinesOfCodeState;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;

public class LinesOfCodeScannerTest extends BaseCodebaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    private LinesOfCodeScanner underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new LinesOfCodeScanner(createFileUtils(), new LinesOfCodeCounter());
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
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        LinesOfCodeState linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
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
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        LinesOfCodeState linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
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
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        LinesOfCodeState linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
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
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        LinesOfCodeState linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
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
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        LinesOfCodeState linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
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
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        LinesOfCodeState linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
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
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        LinesOfCodeState linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
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
        Output<Void, Component> returnValue = underTest.scan(codebase);

        // Then
        assertThat(maskTransformer(returnValue)).isEqualTo(maskTransformer(Output.empty(CACHE_TTL)));
        LinesOfCodeState linesOfCode = getMutatedComponent(returnValue).getLinesOfCode();
        assertThat(linesOfCode).isNotNull();
        assertThat(linesOfCode.getCount()).isEqualTo(6);
        assertThat(linesOfCode.getFileExtensionCounts()).hasSize(3);
        assertThat(linesOfCode.getFileExtensionCounts()).containsExactly(new FileExtensionCount("js", 3),
                new FileExtensionCount("java", 2),
                new FileExtensionCount("txt", 1));
    }
}
