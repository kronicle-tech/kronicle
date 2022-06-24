package tech.kronicle.plugins.doc.testutils;

import tech.kronicle.sdk.models.Tag;
import tech.kronicle.sdk.models.doc.DocFile;
import tech.kronicle.sdk.models.doc.DocFileContentType;
import tech.kronicle.sdk.models.doc.DocState;

import java.util.List;

public final class DocStateUtils {

    public static DocState createDocState(int docStateNumber) {
        return DocState.builder()
                .pluginId("test-plugin-id-" + docStateNumber)
                .id("test-doc-id-" + docStateNumber)
                .dir("test-dir-" + docStateNumber)
                .name("Test Doc Name " + docStateNumber)
                .description("Test Doc Description " + docStateNumber)
                .notes("Test Doc Notes " + docStateNumber)
                .tags(List.of(
                        new Tag("test-doc-tag-key-" + docStateNumber + "-1", "test-doc-tag-value-" + docStateNumber + "-1"),
                        new Tag("test-doc-tag-key-" + docStateNumber + "-2", "test-doc-tag-value-" + docStateNumber + "-2")
                ))
                .files(List.of(
                        createDocFile(docStateNumber, 1)
                ))
                .build();
    }

    private static DocFile createDocFile(int docStateNumber, int docFileNumber) {
        return DocFile.builder()
                .path("test-path-" + docStateNumber + "-" + docFileNumber)
                .mediaType("test-media-type-" + docStateNumber + "-" + docFileNumber)
                .contentType(docFileNumber % 2 == 1 ? DocFileContentType.Text : DocFileContentType.Binary)
                .content("test-content-" + docStateNumber + "-" + docFileNumber)
                .build();
    }

    private DocStateUtils() {
    }
}
