package tech.kronicle.plugins.aws.xray.services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.xray.XRayClient;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;

public class XRayClientFactory {

    public XRayClient createXRayClient(AwsProfileConfig profile, String region) {
        return XRayClient.builder()
                .credentialsProvider(createStaticCredentialsProvider(profile))
                .region(Region.of(region))
                .build();
    }

    private StaticCredentialsProvider createStaticCredentialsProvider(AwsProfileConfig profile) {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(profile.getAccessKeyId(), profile.getSecretAccessKey()));
    }
}
