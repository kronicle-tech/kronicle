package tech.kronicle.plugins.aws.cloudwatchlogs.client;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.utils.AwsCredentialsProviderFactory;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CloudWatchLogsClientFactory {

    private final AwsCredentialsProviderFactory credentialsProviderFactory;

    public CloudWatchLogsClient createCloudWatchLogsClient(AwsProfileAndRegion profileAndRegion) {
        return CloudWatchLogsClient.builder()
                .credentialsProvider(
                        credentialsProviderFactory.createCredentialsProvider(profileAndRegion.getProfile())
                )
                .region(Region.of(profileAndRegion.getRegion()))
                .build();
    }
}
