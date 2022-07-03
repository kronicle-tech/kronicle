package tech.kronicle.plugins.aws.resourcegroupstaggingapi.client;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClient;
import tech.kronicle.plugins.aws.client.ClientFactory;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.utils.AwsCredentialsProviderFactory;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResourceGroupsTaggingApiClientFactory implements ClientFactory<ResourceGroupsTaggingApiClient> {

    private final AwsCredentialsProviderFactory credentialsProviderFactory;

    public ResourceGroupsTaggingApiClient createClient(AwsProfileAndRegion profileAndRegion) {
        return ResourceGroupsTaggingApiClient.builder()
                .credentialsProvider(
                        credentialsProviderFactory.createCredentialsProvider(profileAndRegion)
                )
                .region(Region.of(profileAndRegion.getRegion()))
                .build();
    }
}
