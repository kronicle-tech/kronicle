package tech.kronicle.sdk.models.testutils;

import tech.kronicle.sdk.models.Software;

public final class SoftwareUtils {

    public static Software createSoftware(int softwareNumber) {
        return Software.builder()
                .name("test-software-name-" + softwareNumber)
                .build();
    }

    private SoftwareUtils() {
    }
}
