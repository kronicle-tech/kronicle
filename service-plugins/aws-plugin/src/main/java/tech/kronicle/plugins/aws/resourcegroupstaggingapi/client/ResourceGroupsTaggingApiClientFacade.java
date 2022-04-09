package tech.kronicle.plugins.aws.resourcegroupstaggingapi.client;

import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResourcePage;

import java.util.List;
import java.util.Map;

public interface ResourceGroupsTaggingApiClientFacade extends AutoCloseable {

    ResourceGroupsTaggingApiResourcePage getResources(
            AwsProfileAndRegion profileAndRegion,
            String nextToken
    );

    ResourceGroupsTaggingApiResourcePage getResources(
            AwsProfileAndRegion profileAndRegion,
            List<String> resourceTypeFilters,
            Map<String, List<String>> tagFilters,
            String nextToken
    );

    @Override
    void close();
}
