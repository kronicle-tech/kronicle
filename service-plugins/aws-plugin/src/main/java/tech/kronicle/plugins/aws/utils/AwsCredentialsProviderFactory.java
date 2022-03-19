package tech.kronicle.plugins.aws.utils;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;

import static java.util.Objects.nonNull;

public class AwsCredentialsProviderFactory {

    public AwsCredentialsProvider createCredentialsProvider(AwsProfileConfig profile) {
        if (nonNull(profile.getAccessKeyId())) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create(
                    profile.getAccessKeyId(),
                    profile.getSecretAccessKey()
            ));
        } else {
            return DefaultCredentialsProvider.create();
        }
    }
}
