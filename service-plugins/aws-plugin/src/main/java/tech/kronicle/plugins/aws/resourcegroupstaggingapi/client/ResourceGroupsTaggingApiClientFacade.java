package tech.kronicle.plugins.aws.resourcegroupstaggingapi.client;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClient;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.GetResourcesResponse;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.Tag;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResourcePage;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResourceGroupsTaggingApiClientFacade implements AutoCloseable {

    private final ResourceGroupsTaggingApiClient client;

    @Override
    public void close() {
        client.close();
    }

    public ResourceGroupsTaggingApiResourcePage getResources(String nextToken) {
        return mapResources(
                client.getResources(builder -> builder.paginationToken(nextToken))
        );
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
