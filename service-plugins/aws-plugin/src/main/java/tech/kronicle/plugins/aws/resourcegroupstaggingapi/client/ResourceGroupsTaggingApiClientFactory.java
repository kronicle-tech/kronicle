package tech.kronicle.plugins.aws.resourcegroupstaggingapi.client;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClient;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;

import static tech.kronicle.plugins.aws.utils.StaticCredentialsUtils.createStaticCredentialsProvider;

public class ResourceGroupsTaggingApiClientFactory {

    public ResourceGroupsTaggingApiClient createResourceGroupsTaggingApiClient(AwsProfileAndRegion profileAndRegion) {
        return ResourceGroupsTaggingApiClient.builder()
                .credentialsProvider(createStaticCredentialsProvider(profileAndRegion.getProfile()))
                .region(Region.of(profileAndRegion.getRegion()))
                .build();
    }
}
