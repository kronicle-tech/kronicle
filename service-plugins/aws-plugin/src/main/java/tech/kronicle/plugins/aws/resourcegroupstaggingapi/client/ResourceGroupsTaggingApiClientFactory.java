package tech.kronicle.plugins.aws.resourcegroupstaggingapi.client;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClient;
import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClientBuilder;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.utils.AwsCredentialsProviderFactory;

import javax.inject.Inject;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResourceGroupsTaggingApiClientFactory {

    private final AwsCredentialsProviderFactory credentialsProviderFactory;

    public ResourceGroupsTaggingApiClient createResourceGroupsTaggingApiClient(AwsProfileAndRegion profileAndRegion) {
        ResourceGroupsTaggingApiClientBuilder builder = ResourceGroupsTaggingApiClient.builder()
                .credentialsProvider(
                        credentialsProviderFactory.createCredentialsProvider(profileAndRegion.getProfile())
                );
        if (nonNull(profileAndRegion.getRegion())) {
            builder.region(Region.of(profileAndRegion.getRegion()));
        }
        return builder
                .build();
    }
}
