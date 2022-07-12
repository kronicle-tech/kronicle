package tech.kronicle.plugins.doc.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.doc.testutils.DocStateUtils;
import tech.kronicle.plugintestutils.scanners.BaseCodebaseScannerTest;
import tech.kronicle.sdk.models.Doc;
import tech.kronicle.sdk.models.doc.DocFile;
import tech.kronicle.sdk.models.doc.DocFileContentType;
import tech.kronicle.sdk.models.doc.DocState;
import tech.kronicle.utils.AntStyleIgnoreFileLoader;
import tech.kronicle.utils.FileUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.plugins.doc.testutils.DocUtils.createDocWithDir;
import static tech.kronicle.plugins.doc.testutils.DocUtils.createDocWithFile;

public class DocProcessorTest extends BaseCodebaseScannerTest {

    @Test
    public void processDocsShouldReturnAnEmptyListWhenTheComponentHasNoDocs() {
        // Given
        DocProcessor underTest = createUnderTest();

        // When
        List<DocState> returnValue = underTest.processDocs(getCodebaseDir("NoDocs"), List.of());

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void processDocsShouldReturnAnEmptyDocWhenPassedADirectoryBasedDocAndTheDirectoryDoesNotExist() {
        // Given
        Doc doc1 = createDocWithDir(1, "subdir");
        DocProcessor underTest = createUnderTest();

        // When
        List<DocState> returnValue = underTest.processDocs(getCodebaseDir("NoDocs"), List.of(doc1));

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(
                DocStateUtils.createDocStateWithDir(1, "subdir", List.of())
        );
    }

    @Test
    public void processDocsShouldReturnADocWithFilesWhenPassedADirectoryBasedDocAndTheDirectoryContainsFiles() {
        // Given
        Doc doc1 = createDocWithDir(1, "subdir");
        DocProcessor underTest = createUnderTest();

        // When
        List<DocState> returnValue = underTest.processDocs(getCodebaseDir("MultipleFiles"), List.of(doc1));

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(
                DocStateUtils.createDocStateWithDir(1, "subdir", List.of(
                        DocFile.builder()
                                .path("subdir/example-2.md")
                                .mediaType("text/markdown")
                                .contentType(DocFileContentType.TEXT)
                                .content(
                                        "# Example 2\n" +
                                        "\n" +
                                        "Example 2\n"
                                )
                                .build(),
                        DocFile.builder()
                                .path("subdir/example-3.md")
                                .mediaType("text/markdown")
                                .contentType(DocFileContentType.TEXT)
                                .content(
                                        "# Example 3\n" +
                                                "\n" +
                                                "Example 3\n"
                                )
                                .build()
                ))
        );
    }

    @Test
    public void processDocsShouldReturnAnEmptyDocWhenPassedAFileBasedDocAndTheFileDoesNotExist() {
        // Given
        Doc doc1 = createDocWithFile(1, "subdir/example-2.md");
        DocProcessor underTest = createUnderTest();

        // When
        List<DocState> returnValue = underTest.processDocs(getCodebaseDir("NoDocs"), List.of(doc1));

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(
                DocStateUtils.createDocStateWithFile(1, "subdir/example-2.md", List.of())
        );
    }

    @Test
    public void processDocsShouldReturnADocWithAFileWhenPassedAFileBasedDocAndTheFileDoesExist() {
        // Given
        Doc doc1 = createDocWithFile(1, "subdir/example-2.md");
        DocProcessor underTest = createUnderTest();

        // When
        List<DocState> returnValue = underTest.processDocs(getCodebaseDir("MultipleFiles"), List.of(doc1));

        // Then
        assertThat(returnValue).containsExactlyInAnyOrder(
                DocStateUtils.createDocStateWithFile(1, "subdir/example-2.md", List.of(
                        DocFile.builder()
                                .path("subdir/example-2.md")
                                .mediaType("text/markdown")
                                .contentType(DocFileContentType.TEXT)
                                .content(
                                        "# Example 2\n" +
                                                "\n" +
                                                "Example 2\n"
                                )
                                .build()
                ))
        );
    }

    @Test
    public void processDocsShouldHandleABinaryFile() {
        // Given
        Doc doc1 = createDocWithFile(1, "test.png");
        DocProcessor underTest = createUnderTest();

        // When
        List<DocState> returnValue = underTest.processDocs(getCodebaseDir("BinaryContent"), List.of(doc1));

        // Then
        String base64Content = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAOFJREFUOE/d07ErBVAUx/HPz/JKFr2QYrAblcVbyCarXZRZyWqV5B+wyOQvMTAaDQYDI6GUOrp6TI/lbc50h3u+99f3nBtDVobs9x8AVfXU9zCKN3wk6f7lpqo2sJhk98dBVb0n6VTVGG7QILdYwDG2MYJDbGEar4MAe+glWa+qaxzhFGu4wjyWsZJkdRDgsn/pla8pnWAWO3hAi7/0F6C92E2yWVUt8gQ6eMQZ5nDeQEl6gxJMoaVoNYl9HPQFN9HtfIE7PP+6SFXVJL4keW+kqppJcv89nX668f+wicP+xk8mg1fzwuFtGgAAAABJRU5ErkJggg==";
        assertThat(returnValue).containsExactlyInAnyOrder(
                DocStateUtils.createDocStateWithFile(1, "test.png", List.of(
                        DocFile.builder()
                                .path("test.png")
                                .mediaType("image/png")
                                .contentType(DocFileContentType.BINARY)
                                .content(base64Content)
                                .build()
                ))
        );
    }

    private DocProcessor createUnderTest() {
        return new DocProcessor(new FileTypeRegistry(), new FileUtils(new AntStyleIgnoreFileLoader()));
    }
}
