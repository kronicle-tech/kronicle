package tech.kronicle.plugins.aws.resourcegroupstaggingapi.client;

import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResourcePage;

import java.util.List;
import java.util.Map;

public interface ResourceGroupsTaggingApiClientFacade extends AutoCloseable {

    ResourceGroupsTaggingApiResourcePage getResources(String nextToken);

    ResourceGroupsTaggingApiResourcePage getResources(
            List<String> resourceTypeFilters,
            Map<String, List<String>> tagFilters,
            String nextToken
    );

    @Override
    void close();
}
