package tech.kronicle.plugins.aws.resourcegroupstaggingapi.utils;

import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;

import java.util.Optional;

public final class ResourceUtils {

    public static Optional<String> getOptionalResourceTagValue(ResourceGroupsTaggingApiResource resource, String name) {
        return resource.getTags().stream()
                .filter(tag -> tag.getKey().equals(name))
                .findFirst()
                .map(ResourceGroupsTaggingApiTag::getValue);
    }

    public static String getResourceTagValue(ResourceGroupsTaggingApiResource resource, String name) {
        return getOptionalResourceTagValue(resource, name).get();
    }

    private ResourceUtils() {
    }
}
