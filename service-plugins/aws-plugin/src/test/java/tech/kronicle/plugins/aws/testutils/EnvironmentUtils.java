package tech.kronicle.plugins.aws.testutils;

public final class EnvironmentUtils {

    public static String createOverrideEnvironmentId(int environmentNumber) {
        return "test-override-environment-id-" + environmentNumber;
    }

    private EnvironmentUtils() {
    }
}
