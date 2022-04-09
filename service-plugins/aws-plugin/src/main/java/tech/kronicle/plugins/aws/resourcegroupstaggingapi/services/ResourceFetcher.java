package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.client.ResourceGroupsTaggingApiClientFacade;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static tech.kronicle.plugins.aws.utils.PageFetcher.fetchAllPages;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResourceFetcher {

    private final ResourceGroupsTaggingApiClientFacade clientFacade;

    public List<ResourceGroupsTaggingApiResource> getResources(AwsProfileAndRegion profileAndRegion) {
        return fetchAllPages(nextToken -> clientFacade.getResources(profileAndRegion, nextToken));
    }

    public List<ResourceGroupsTaggingApiResource> getResources(
            AwsProfileAndRegion profileAndRegion,
            List<String> resourceTypeFilters,
            Map<String, List<String>> tagFilters
    ) {
        return fetchAllPages(nextToken -> clientFacade.getResources(
                profileAndRegion,
                resourceTypeFilters,
                tagFilters,
                nextToken
        ));
    }
}
