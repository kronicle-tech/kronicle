package tech.kronicle.sdk.models.testutils;

import tech.kronicle.sdk.models.SoftwareRepository;

public final class SoftwareRepositoryUtils {

    public static SoftwareRepository createSoftwareRepository(int softwareRepositoryNumber) {
        return SoftwareRepository.builder()
                .url("https://example.com/test-software-repository-url-" + softwareRepositoryNumber)
                .build();
    }

    private SoftwareRepositoryUtils() {
    }
}
