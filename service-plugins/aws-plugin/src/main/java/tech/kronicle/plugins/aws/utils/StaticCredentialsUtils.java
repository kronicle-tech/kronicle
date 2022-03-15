package tech.kronicle.plugins.aws.utils;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;

public final class StaticCredentialsUtils {

    public static StaticCredentialsProvider createStaticCredentialsProvider(AwsProfileConfig profile) {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(
                profile.getAccessKeyId(),
                profile.getSecretAccessKey()
        ));
    }

    private StaticCredentialsUtils() {
    }
}
