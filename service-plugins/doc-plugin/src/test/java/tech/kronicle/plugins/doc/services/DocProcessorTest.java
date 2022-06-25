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
import static tech.kronicle.plugins.doc.testutils.DocStateUtils.createDocStateWithDir;
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
        assertThat(returnValue).containsExactly(
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
        assertThat(returnValue).containsExactly(
                DocStateUtils.createDocStateWithDir(1, "subdir", List.of(
                        DocFile.builder()
                                .path("subdir/example-2.md")
                                .mediaType("text/markdown")
                                .contentType(DocFileContentType.Text)
                                .content(
                                        "# Example 2\n" +
                                        "\n" +
                                        "Example 2\n"
                                )
                                .build(),
                        DocFile.builder()
                                .path("subdir/example-3.md")
                                .mediaType("text/markdown")
                                .contentType(DocFileContentType.Text)
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
        assertThat(returnValue).containsExactly(
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
        assertThat(returnValue).containsExactly(
                DocStateUtils.createDocStateWithFile(1, "subdir/example-2.md", List.of(
                        DocFile.builder()
                                .path("subdir/example-2.md")
                                .mediaType("text/markdown")
                                .contentType(DocFileContentType.Text)
                                .content(
                                        "# Example 2\n" +
                                                "\n" +
                                                "Example 2\n"
                                )
                                .build()
                ))
        );
    }

    private DocProcessor createUnderTest() {
        return new DocProcessor(new FileTypeRegistry(), new FileUtils(new AntStyleIgnoreFileLoader()));
    }
}
