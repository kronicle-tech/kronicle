package tech.kronicle.plugins.aws.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.config.AwsTagKeysConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.models.ResourceIdsByProfileAndRegionAndComponent;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.services.ResourceFetcher;
import tech.kronicle.sdk.models.Component;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.aws.testutils.AwsProfileAndRegionUtils.createProfile;
import static tech.kronicle.plugins.aws.testutils.ComponentUtils.createComponent;
import static tech.kronicle.plugins.aws.testutils.ResourceGroupsTaggingApiResourceUtils.TEST_COMPONENT_TAG_KEY;
import static tech.kronicle.plugins.aws.testutils.ResourceGroupsTaggingApiResourceUtils.createResource;

@ExtendWith(MockitoExtension.class)
public class TaggedResourceFinderTest {

    @Mock
    private ResourceFetcher resourceFetcher;

    @Test
    public void getResourceIdsByProfileAndRegionAndComponentShouldResourceIdsByProfileAndRegionAndComponent() {
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
                new AwsTagKeysConfig(
                        TEST_COMPONENT_TAG_KEY,
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
        mockGetResources(profile1AndRegion1, resourceType, List.of(
                createResource(1, component1.getId()),
                createResource(2, component1.getId()),
                createResource(3, component2.getId()),
                createResource(4, component2.getId())
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
        ResourceIdsByProfileAndRegionAndComponent returnValue = underTest.getResourceIdsByProfileAndRegionAndComponent(resourceType);

        // Then
        assertThat(returnValue.getResourceIds(profile1AndRegion1, component1)).containsExactly("test-resource-id-1", "test-resource-id-2");
        assertThat(returnValue.getResourceIds(profile1AndRegion1, component2)).containsExactly("test-resource-id-3", "test-resource-id-4");
        assertThat(returnValue.getResourceIds(profile1AndRegion2, component1)).isEmpty();
        assertThat(returnValue.getResourceIds(profile1AndRegion2, component2)).isEmpty();
        assertThat(returnValue.getResourceIds(profile2AndRegion1, component1)).isEmpty();
        assertThat(returnValue.getResourceIds(profile2AndRegion1, component2)).isEmpty();
        assertThat(returnValue.getResourceIds(profile2AndRegion2, component1)).containsExactly("test-resource-id-5", "test-resource-id-6");
        assertThat(returnValue.getResourceIds(profile2AndRegion2, component2)).containsExactly("test-resource-id-7", "test-resource-id-8");
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
