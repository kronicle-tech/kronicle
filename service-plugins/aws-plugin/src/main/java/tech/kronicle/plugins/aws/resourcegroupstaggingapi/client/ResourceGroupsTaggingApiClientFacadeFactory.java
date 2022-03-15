package tech.kronicle.plugins.aws.resourcegroupstaggingapi.client;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResourceGroupsTaggingApiClientFacadeFactory {

    private final ResourceGroupsTaggingApiClientFactory clientFactory;

    public ResourceGroupsTaggingApiClientFacade createResourceGroupsTaggingApiClientFacade(
            AwsProfileAndRegion profileAndRegion
    ) {
        return new ResourceGroupsTaggingApiClientFacadeImpl(
                clientFactory.createResourceGroupsTaggingApiClient(profileAndRegion)
        );
    }
}
