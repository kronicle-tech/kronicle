package tech.kronicle.plugins.aws.utils;

import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;

import static java.util.Objects.nonNull;

public class AwsCredentialsProviderFactory {

    public AwsCredentialsProvider createCredentialsProvider(AwsProfileAndRegion profileAndRegion) {
        return assumeRoleIfNeeded(profileAndRegion, create(profileAndRegion.getProfile()));
    }

    private AwsCredentialsProvider create(AwsProfileConfig profile) {
        if (nonNull(profile.getAccessKeyId())) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create(
                    profile.getAccessKeyId(),
                    profile.getSecretAccessKey()
            ));
        } else {
            return DefaultCredentialsProvider.create();
        }
    }

    private AwsCredentialsProvider assumeRoleIfNeeded(
            AwsProfileAndRegion profileAndRegion,
            AwsCredentialsProvider credentialsProvider
    ) {
        if (nonNull(profileAndRegion.getProfile().getRoleArn())) {
            return assumeRole(
                    profileAndRegion.getRegion(),
                    profileAndRegion.getProfile().getRoleArn(),
                    credentialsProvider
            );
        } else {
            return credentialsProvider;
        }
    }

    private AwsCredentialsProvider assumeRole(String region, String roleArn, AwsCredentialsProvider credentialsProvider) {
        try (StsClient stsClient = StsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build()) {

            AssumeRoleRequest roleRequest = AssumeRoleRequest.builder()
                    .roleArn(roleArn)
                    .roleSessionName("kronicle-service")
                    .build();

            AssumeRoleResponse roleResponse = stsClient.assumeRole(roleRequest);
            Credentials credentials = roleResponse.credentials();
            return StaticCredentialsProvider.create(AwsSessionCredentials.create(
                    credentials.accessKeyId(),
                    credentials.secretAccessKey(),
                    credentials.sessionToken()
            ));
        }
    }
}
