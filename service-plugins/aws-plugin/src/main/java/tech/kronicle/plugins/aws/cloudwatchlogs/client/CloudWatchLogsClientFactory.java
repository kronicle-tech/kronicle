package tech.kronicle.plugins.aws.cloudwatchlogs.client;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import tech.kronicle.plugins.aws.client.ClientFactory;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.utils.AwsCredentialsProviderFactory;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CloudWatchLogsClientFactory implements ClientFactory<CloudWatchLogsClient> {

    private final AwsCredentialsProviderFactory credentialsProviderFactory;

    public CloudWatchLogsClient createClient(AwsProfileAndRegion profileAndRegion) {
        return CloudWatchLogsClient.builder()
                .credentialsProvider(
                        credentialsProviderFactory.createCredentialsProvider(profileAndRegion)
                )
                .region(Region.of(profileAndRegion.getRegion()))
                .build();
    }
}
