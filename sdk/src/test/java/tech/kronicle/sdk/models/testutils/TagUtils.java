package tech.kronicle.sdk.models.testutils;

import tech.kronicle.sdk.models.Tag;

public final class TagUtils {

    public static Tag createTag(int tagNumber) {
        return new Tag(
                "test-tag-key-" + tagNumber,
                "test-tag-value-" + tagNumber
        );
    }

    private TagUtils() {
    }
}
