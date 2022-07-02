package tech.kronicle.plugins.aws.testutils;

import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiTag;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public final class ResourceGroupsTaggingApiResourceUtils {

    public static final String TEST_ALIASES_TAG_KEY = "test-aliases-tag-key";
    public static final String TEST_COMPONENT_TAG_KEY = "test-component-tag-key";
    public static final String TEST_DESCRIPTION_TAG_KEY = "test-description-tag-key";
    public static final String TEST_ENVIRONMENT_TAG_KEY = "test-environment-tag-key";
    public static final String TEST_TEAM_TAG_KEY = "test-team-tag-key";

    public static ResourceGroupsTaggingApiResource createResource(
            int resourceNumber
    ) {
        return createResource(resourceNumber, null, null);
    }

    public static ResourceGroupsTaggingApiResource createResource(
            int resourceNumber,
            String componentId
    ) {
        return createResource(resourceNumber, componentId, null);
    }

    public static ResourceGroupsTaggingApiResource createResource(
            int resourceNumber,
            String componentId,
            String environmentId
    ) {
        return new ResourceGroupsTaggingApiResource(
                createArn(resourceNumber),
                createResourceTags(componentId, environmentId)
        );
    }

    private static String createArn(int resourceNumber) {
        return "arn:aws:lambda:test-region:123456789012:function:test-resource-id-" + resourceNumber;
    }

    private static List<ResourceGroupsTaggingApiTag> createResourceTags(String componentId, String environmentId) {
        List<ResourceGroupsTaggingApiTag> tags = new ArrayList<>();
        if (nonNull(componentId)) {
            tags.add(new ResourceGroupsTaggingApiTag(
                    TEST_COMPONENT_TAG_KEY,
                    componentId
            ));
        }
        if (nonNull(environmentId)) {
            tags.add(new ResourceGroupsTaggingApiTag(
                    TEST_ENVIRONMENT_TAG_KEY,
                    environmentId
            ));
        }
        return tags;
    }

    private ResourceGroupsTaggingApiResourceUtils() {
    }
}
