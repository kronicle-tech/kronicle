package tech.kronicle.plugins.aws.resourcegroupstaggingapi.client;

import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResourcePage;

public interface ResourceGroupsTaggingApiClientFacade extends AutoCloseable {

    ResourceGroupsTaggingApiResourcePage getResources(String nextToken);

    @Override
    void close();
}
