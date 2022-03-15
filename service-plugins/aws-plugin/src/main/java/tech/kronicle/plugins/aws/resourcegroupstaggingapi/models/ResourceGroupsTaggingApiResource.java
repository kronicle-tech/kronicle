package tech.kronicle.plugins.aws.resourcegroupstaggingapi.models;

import lombok.Value;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
public class ResourceGroupsTaggingApiResource {

    String arn;
    List<ResourceGroupsTaggingApiTag> tags;

    public ResourceGroupsTaggingApiResource(String arn, List<ResourceGroupsTaggingApiTag> tags) {
        this.arn = arn;
        this.tags = createUnmodifiableList(tags);
    }
}
