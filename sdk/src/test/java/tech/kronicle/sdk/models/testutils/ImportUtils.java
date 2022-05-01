package tech.kronicle.sdk.models.testutils;

import tech.kronicle.sdk.models.Import;

public final class ImportUtils {

    public static Import createImport(int importNumber) {
        return Import.builder()
                .name("test-import-name-" + importNumber)
                .build();
    }

    private ImportUtils() {
    }
}
