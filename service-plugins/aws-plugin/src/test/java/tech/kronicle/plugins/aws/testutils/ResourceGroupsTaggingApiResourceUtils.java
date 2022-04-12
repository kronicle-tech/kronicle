package tech.kronicle.plugins.aws.testutils;

import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;

import java.util.List;

import static java.util.Objects.isNull;

public final class ResourceGroupsTaggingApiResourceUtils {

    public static final String TEST_COMPONENT_TAG_KEY = "test-component-tag-key";

    public static ResourceGroupsTaggingApiResource createResource(
            int resourceNumber
    ) {
        return createResource(resourceNumber, null);
    }

    public static ResourceGroupsTaggingApiResource createResource(
            int resourceNumber,
            String componentId
    ) {
        return new ResourceGroupsTaggingApiResource(
                createArn(resourceNumber),
                createResourceTags(componentId)
        );
    }

    private static String createArn(int resourceNumber) {
        return "arn:aws:lambda:test-region:123456789012:function:test-resource-id-" + resourceNumber;
    }

    private static List<ResourceGroupsTaggingApiTag> createResourceTags(String componentId) {
        if (isNull(componentId)) {
            return List.of();
        }
        return List.of(
                new ResourceGroupsTaggingApiTag(
                        TEST_COMPONENT_TAG_KEY,
                        componentId
                )
        );
    }

    private ResourceGroupsTaggingApiResourceUtils() {
    }
}
