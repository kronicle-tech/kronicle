package tech.kronicle.utils;

import org.junit.jupiter.api.Test;
import tech.kronicle.utils.AntStyleIgnoreFileLoader;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AntStyleIgnoreFileLoaderTest {

    private AntStyleIgnoreFileLoader underTest = new AntStyleIgnoreFileLoader();

    @Test
    public void loadShouldSplitLinesDelimitedByANewLine() {
        // Given
        String content = "line 1\nline 2";

        // When
        List<String> returnValue = underTest.load(content);

        // Then
        assertThat(returnValue).containsExactly("line 1", "line 2");
    }

    @Test
    public void loadShouldSplitLinesDelimitedByACarriageReturnNewLine() {
        // Given
        String content = "line 1\r\nline 2";

        // When
        List<String> returnValue = underTest.load(content);

        // Then
        assertThat(returnValue).containsExactly("line 1", "line 2");
    }

    @Test
    public void loadShouldIgnoreALineStartingWithAHash() {
        // Given
        String content = "line 1\n#\nline 2";

        // When
        List<String> returnValue = underTest.load(content);

        // Then
        assertThat(returnValue).containsExactly("line 1", "line 2");
    }

    @Test
    public void loadShouldIgnoreALineStartingWithAHashFollowedByText() {
        // Given
        String content = "line 1\n# This is a comment\nline 2";

        // When
        List<String> returnValue = underTest.load(content);

        // Then
        assertThat(returnValue).containsExactly("line 1", "line 2");
    }

    @Test
    public void loadShouldIgnoreALineStartingWithASpaceAndThenAHash() {
        // Given
        String content = "line 1\n  #\nline 2";

        // When
        List<String> returnValue = underTest.load(content);

        // Then
        assertThat(returnValue).containsExactly("line 1", "line 2");
    }

    @Test
    public void loadShouldIgnoreALineStartingWithTabsAndThenAHash() {
        // Given
        String content = "line 1\n\t\t#\nline 2";

        // When
        List<String> returnValue = underTest.load(content);

        // Then
        assertThat(returnValue).containsExactly("line 1", "line 2");
    }

    @Test
    public void loadShouldIgnoreAnEmptyLine() {
        // Given
        String content = "line 1\n\nline 2";

        // When
        List<String> returnValue = underTest.load(content);

        // Then
        assertThat(returnValue).containsExactly("line 1", "line 2");
    }

    @Test
    public void loadShouldIgnoreALineContainingOnlySpaces() {
        // Given
        String content = "line 1\n  \nline 2";

        // When
        List<String> returnValue = underTest.load(content);

        // Then
        assertThat(returnValue).containsExactly("line 1", "line 2");
    }

    @Test
    public void loadShouldIgnoreALineContainingOnlyTabs() {
        // Given
        String content = "line 1\n\t\t\nline 2";

        // When
        List<String> returnValue = underTest.load(content);

        // Then
        assertThat(returnValue).containsExactly("line 1", "line 2");
    }
}
