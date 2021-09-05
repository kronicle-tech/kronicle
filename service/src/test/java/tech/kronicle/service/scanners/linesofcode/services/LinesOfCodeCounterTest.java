package tech.kronicle.service.scanners.linesofcode.services;

import com.google.common.base.Ascii;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class LinesOfCodeCounterTest {

    private final LinesOfCodeCounter underTest = new LinesOfCodeCounter();

    @Test
    public void shouldDetectTextFileThatContainsOnlyAsciiTextCharacters() throws IOException {
        // Given
        String string = "\t\n\f\r!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

        // When
        LinesOfCodeCounter.LinesOfCodeCountResult result = underTest.countLinesOfCode(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));

        // Then
        assertThat(result.getFileType()).isEqualTo(LinesOfCodeCounter.FileType.TEXT);
        assertThat(result.getLinesOfCodeCount()).isEqualTo(2);
    }

    @Test
    public void shouldDetectTextFileThatContainsASingleLine() throws IOException {
        // Given
        String string = "line";

        // When
        LinesOfCodeCounter.LinesOfCodeCountResult result = underTest.countLinesOfCode(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));

        // Then
        assertThat(result.getFileType()).isEqualTo(LinesOfCodeCounter.FileType.TEXT);
        assertThat(result.getLinesOfCodeCount()).isEqualTo(1);
    }

    @Test
    public void shouldDetectTextFileThatContains95PercentAsciiNonText() throws IOException {
        // Given
        String string = getStringContainingAsciiTextAndAsciiNonText(95, Ascii.VT);

        // When
        LinesOfCodeCounter.LinesOfCodeCountResult result = underTest.countLinesOfCode(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));

        // Then
        assertThat(result.getFileType()).isEqualTo(LinesOfCodeCounter.FileType.TEXT);
        assertThat(result.getLinesOfCodeCount()).isEqualTo(1);
    }

    @Test
    public void shouldDetectTextFileAsBinaryThatContains96PercentAsciiNonText() throws IOException {
        // Given
        String string = getStringContainingAsciiTextAndAsciiNonText(96, Ascii.VT);

        // When
        LinesOfCodeCounter.LinesOfCodeCountResult result = underTest.countLinesOfCode(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));

        // Then
        assertThat(result.getFileType()).isEqualTo(LinesOfCodeCounter.FileType.BINARY);
        assertThat(result.getLinesOfCodeCount()).isEqualTo(0);
    }

    @Test
    public void shouldDetectTextFileAsBinaryThatContainsLowValueAsciiNonTextCharacter() throws IOException {
        // Given
        String string = getStringContainingAsciiTextAndAsciiNonText(1, Ascii.BS);

        // When
        LinesOfCodeCounter.LinesOfCodeCountResult result = underTest.countLinesOfCode(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));

        // Then
        assertThat(result.getFileType()).isEqualTo(LinesOfCodeCounter.FileType.BINARY);
        assertThat(result.getLinesOfCodeCount()).isEqualTo(0);
    }

    private String getStringContainingAsciiTextAndAsciiNonText(int asciiNonTextPercentage, byte asciiNonTextCharacter) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int index = 0; index < 100; index++) {
            stringBuilder.append(index < asciiNonTextPercentage ? (char) asciiNonTextCharacter : 'a');
        }

        return stringBuilder.toString();
    }
}
