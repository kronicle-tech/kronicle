package tech.kronicle.plugins.aws.testutils;

import tech.kronicle.plugins.aws.config.AwsProfileConfig;

import java.util.List;

public final class AwsProfileAndRegionUtils {

    public static AwsProfileConfig createProfile(int profileNumber) {
        return new AwsProfileConfig(
                "test-access-key-id-" + profileNumber,
                "test-secret-access-key-" + profileNumber,
                List.of(
                        createRegion(profileNumber, 1),
                        createRegion(profileNumber, 2)
                ),
                "test-environment-id-" + profileNumber
        );
    }

    public static String createRegion(int profileNumber, int regionNumber) {
        return "test-region-" + profileNumber + "-" + regionNumber;
    }

    private AwsProfileAndRegionUtils() {
    }
}
