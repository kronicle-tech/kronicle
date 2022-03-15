package tech.kronicle.plugins.aws.resourcegroupstaggingapi.models;

import lombok.Value;
import tech.kronicle.plugins.aws.models.Page;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
public class ResourceGroupsTaggingApiResourcePage implements Page<ResourceGroupsTaggingApiResource> {

    List<ResourceGroupsTaggingApiResource> items;
    String nextPage;

    public ResourceGroupsTaggingApiResourcePage(List<ResourceGroupsTaggingApiResource> items, String nextPage) {
        this.items = createUnmodifiableList(items);
        this.nextPage = nextPage;
    }
}
