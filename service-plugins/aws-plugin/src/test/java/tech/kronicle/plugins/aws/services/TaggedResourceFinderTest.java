package tech.kronicle.plugins.aws.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.config.AwsTagKeysConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.models.TaggedResource;
import tech.kronicle.plugins.aws.models.TaggedResourcesByProfileAndRegionAndComponent;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.services.ResourceFetcher;
import tech.kronicle.sdk.models.Component;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.aws.testutils.AwsProfileAndRegionUtils.createProfile;
import static tech.kronicle.plugins.aws.testutils.ComponentUtils.createComponent;
import static tech.kronicle.plugins.aws.testutils.EnvironmentUtils.createOverrideEnvironmentId;
import static tech.kronicle.plugins.aws.testutils.ResourceGroupsTaggingApiResourceUtils.TEST_COMPONENT_TAG_KEY;
import static tech.kronicle.plugins.aws.testutils.ResourceGroupsTaggingApiResourceUtils.TEST_ENVIRONMENT_TAG_KEY;
import static tech.kronicle.plugins.aws.testutils.ResourceGroupsTaggingApiResourceUtils.createResource;

@ExtendWith(MockitoExtension.class)
public class TaggedResourceFinderTest {

    @Mock
    private ResourceFetcher resourceFetcher;

    @Test
    public void getTaggedResourcesByProfileAndRegionAndComponentShouldGetTaggedResourcesByProfileAndRegionAndComponent() {
        // Given
        AwsProfileConfig profile1 = createProfile(1);
        AwsProfileConfig profile2 = createProfile(2);
        AwsProfileAndRegion profile1AndRegion1 = new AwsProfileAndRegion(profile1, profile1.getRegions().get(0));
        AwsProfileAndRegion profile1AndRegion2 = new AwsProfileAndRegion(profile1, profile1.getRegions().get(1));
        AwsProfileAndRegion profile2AndRegion1 = new AwsProfileAndRegion(profile2, profile2.getRegions().get(0));
        AwsProfileAndRegion profile2AndRegion2 = new AwsProfileAndRegion(profile2, profile2.getRegions().get(1));
        AwsConfig config = new AwsConfig(
                List.of(
                        profile1,
                        profile2
                ),
                null,
                null,
                null,
                null,
                new AwsTagKeysConfig(
                        TEST_COMPONENT_TAG_KEY,
                        TEST_ENVIRONMENT_TAG_KEY,
                        null
                ),
                null
        );
        TaggedResourceFinder underTest = new TaggedResourceFinder(
                resourceFetcher,
                config
        );
        String resourceType = "test-resource-type";
        Component component1 = createComponent(1);
        Component component2 = createComponent(2);
        String overrideEnvironmentId1 = createOverrideEnvironmentId(1);
        String overrideEnvironmentId2 = createOverrideEnvironmentId(2);
        mockGetResources(profile1AndRegion1, resourceType, List.of(
                createResource(1, component1.getId(), overrideEnvironmentId1),
                createResource(2, component1.getId()),
                createResource(3, component2.getId()),
                createResource(4, component2.getId(), overrideEnvironmentId2)
        ));
        mockGetResources(profile1AndRegion2, resourceType, List.of());
        mockGetResources(profile2AndRegion1, resourceType, List.of());
        mockGetResources(profile2AndRegion2, resourceType, List.of(
                createResource(5, component1.getId()),
                createResource(6, component1.getId()),
                createResource(7, component2.getId()),
                createResource(8, component2.getId())
        ));

        // When
        TaggedResourcesByProfileAndRegionAndComponent returnValue = underTest.getTaggedResourcesByProfileAndRegionAndComponent(resourceType);

        // Then
        assertThat(returnValue.getTaggedResources(profile1AndRegion1, component1)).containsExactly(
                createTaggedResource(overrideEnvironmentId1, 1),
                createTaggedResource(profile1AndRegion1, 2)
        );
        assertThat(returnValue.getTaggedResources(profile1AndRegion1, component2)).containsExactly(
                createTaggedResource(profile1AndRegion1, 3),
                createTaggedResource(overrideEnvironmentId2, 4)
        );
        assertThat(returnValue.getTaggedResources(profile1AndRegion2, component1)).isEmpty();
        assertThat(returnValue.getTaggedResources(profile1AndRegion2, component2)).isEmpty();
        assertThat(returnValue.getTaggedResources(profile2AndRegion1, component1)).isEmpty();
        assertThat(returnValue.getTaggedResources(profile2AndRegion1, component2)).isEmpty();
        assertThat(returnValue.getTaggedResources(profile2AndRegion2, component1)).containsExactly(
                createTaggedResource(profile2AndRegion2, 5),
                createTaggedResource(profile2AndRegion2, 6)
        );
        assertThat(returnValue.getTaggedResources(profile2AndRegion2, component2)).containsExactly(
                createTaggedResource(profile2AndRegion2, 7),
                createTaggedResource(profile2AndRegion2, 8)
        );
    }

    private TaggedResource createTaggedResource(AwsProfileAndRegion profileAndRegion, int taggedResourceNumber) {
        return new TaggedResource(
                "test-resource-id-" + taggedResourceNumber,
                profileAndRegion.getProfile().getEnvironmentId()
        );
    }

    private TaggedResource createTaggedResource(String environmentId, int taggedResourceNumber) {
        return new TaggedResource(
                "test-resource-id-" + taggedResourceNumber,
                environmentId
        );
    }

    private void mockGetResources(
            AwsProfileAndRegion profileAndRegion,
            String resourceType, List<ResourceGroupsTaggingApiResource> resources
    ) {
        when(resourceFetcher.getResources(
                profileAndRegion,
                List.of(resourceType),
                Map.of(TEST_COMPONENT_TAG_KEY, List.of())
        )).thenReturn(resources);
    }
}
