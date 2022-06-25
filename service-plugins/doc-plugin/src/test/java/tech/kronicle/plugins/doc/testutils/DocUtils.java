package tech.kronicle.plugins.doc.testutils;

import tech.kronicle.sdk.models.Doc;
import tech.kronicle.sdk.models.Tag;

import java.util.List;

public final class DocUtils {

    public static Doc createDocWithDir(int docNumber) {
        return createDocWithDir(docNumber, "test-dir-" + docNumber);
    }

    public static Doc createDocWithDir(int docNumber, String dir) {
        return Doc.builder()
                .id("test-doc-id-" + docNumber)
                .dir(dir)
                .name("Test Doc Name " + docNumber)
                .description("Test Doc Description " + docNumber)
                .notes("Test Doc Notes " + docNumber)
                .tags(List.of(
                        new Tag("test-doc-tag-key-" + docNumber + "-1", "test-doc-tag-value-" + docNumber + "-1"),
                        new Tag("test-doc-tag-key-" + docNumber + "-2", "test-doc-tag-value-" + docNumber + "-2")
                ))
                .build();
    }

    public static Doc createDocWithFile(int docNumber) {
        return createDocWithDir(docNumber, "test-file-" + docNumber);
    }

    public static Doc createDocWithFile(int docNumber, String file) {
        return Doc.builder()
                .id("test-doc-id-" + docNumber)
                .file(file)
                .name("Test Doc Name " + docNumber)
                .description("Test Doc Description " + docNumber)
                .notes("Test Doc Notes " + docNumber)
                .tags(List.of(
                        new Tag("test-doc-tag-key-" + docNumber + "-1", "test-doc-tag-value-" + docNumber + "-1"),
                        new Tag("test-doc-tag-key-" + docNumber + "-2", "test-doc-tag-value-" + docNumber + "-2")
                ))
                .build();
    }

    private DocUtils() {
    }
}
