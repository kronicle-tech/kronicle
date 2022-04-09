package tech.kronicle.plugins.aws.resourcegroupstaggingapi.client;

import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClient;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.GetResourcesResponse;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.Tag;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.TagFilter;
import tech.kronicle.plugins.aws.client.BaseClientFacade;
import tech.kronicle.plugins.aws.client.ClientFactory;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResourcePage;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class ResourceGroupsTaggingApiClientFacadeImpl extends BaseClientFacade<ResourceGroupsTaggingApiClient>
        implements ResourceGroupsTaggingApiClientFacade {

    @Inject
    public ResourceGroupsTaggingApiClientFacadeImpl(ClientFactory<ResourceGroupsTaggingApiClient> clientFactory) {
        super(clientFactory);
    }

    public ResourceGroupsTaggingApiResourcePage getResources(
            AwsProfileAndRegion profileAndRegion,
            String nextToken
    ) {
        return mapResources(
                getClient(profileAndRegion).getResources(builder -> builder.paginationToken(nextToken))
        );
    }

    public ResourceGroupsTaggingApiResourcePage getResources(
            AwsProfileAndRegion profileAndRegion,
            List<String> resourceTypeFilters,
            Map<String, List<String>> tagFilters,
            String nextToken
    ) {
        return mapResources(
                getClient(profileAndRegion).getResources(builder -> builder
                        .resourceTypeFilters(resourceTypeFilters)
                        .tagFilters(mapTagFilters(tagFilters))
                        .paginationToken(nextToken))
        );
    }

    private List<TagFilter> mapTagFilters(Map<String, List<String>> tagFilters) {
        return tagFilters.entrySet().stream()
                .map(entry -> TagFilter.builder()
                        .key(entry.getKey())
                        .values(entry.getValue())
                        .build()
                )
                .collect(Collectors.toList());
    }

    private ResourceGroupsTaggingApiResourcePage mapResources(GetResourcesResponse resources) {
        return new ResourceGroupsTaggingApiResourcePage(
                mapResources(resources.resourceTagMappingList()),
                resources.paginationToken()
        );
    }

    private List<ResourceGroupsTaggingApiResource> mapResources(List<ResourceTagMapping> resourceTagMappingList) {
        return resourceTagMappingList.stream()
                .map(this::mapResource)
                .collect(Collectors.toList());
    }

    private ResourceGroupsTaggingApiResource mapResource(ResourceTagMapping resourceTagMapping) {
        return new ResourceGroupsTaggingApiResource(
                resourceTagMapping.resourceARN(),
                mapTags(resourceTagMapping.tags())
        );
    }

    private List<ResourceGroupsTaggingApiTag> mapTags(List<Tag> tags) {
        if (isNull(tags)) {
            return List.of();
        }

        return tags.stream()
                .map(this::mapTag)
                .collect(Collectors.toList());
    }

    private ResourceGroupsTaggingApiTag mapTag(Tag tag) {
        return new ResourceGroupsTaggingApiTag(tag.key(), tag.value());
    }
}
