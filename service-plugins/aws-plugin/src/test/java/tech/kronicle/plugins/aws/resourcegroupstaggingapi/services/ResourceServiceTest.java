package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.sdk.models.Component;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.aws.testutils.ResourceGroupsTaggingApiResourceUtils.createResource;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    private static final String TEST_ENVIRONMENT_ID = "test-environment-id";

    public ResourceService underTest;
    @Mock
    public ResourceFetcher fetcher;
    @Mock
    public ResourceMapper mapper;

    @Test
    public void getDependenciesShouldFetchResourcesForAProfileAndRegionAndMapResourcesToComponents() {
        // Given
        AwsProfileConfig profile = new AwsProfileConfig(
                null,
                null,
                List.of("test-region-1"),
                TEST_ENVIRONMENT_ID
        );
        underTest = createUnderTest(List.of(profile));
        List<ResourceGroupsTaggingApiResource> services = List.of(
                createResource(1),
                createResource(2)
        );
        when(fetcher.getResources(new AwsProfileAndRegion(profile, "test-region-1"))).thenReturn(services);
        List<Component> components = List.of(
                Component.builder()
                        .id("test-component-id-1")
                        .build(),
                Component.builder()
                        .id("test-component-id-2")
                        .build()
        );
        when(mapper.mapResourcesToComponents(profile.getEnvironmentId(), services)).thenReturn(components);

        // When
        List<Component> returnValue = underTest.getComponents();

        // Then
        assertThat(returnValue).isEqualTo(components);
    }

    @Test
    public void getDependenciesShouldFetchResourcesForAProfileAndMultipleRegionsAndMapResourcesToComponents() {
        // Given
        AwsProfileConfig profile = new AwsProfileConfig(
                null,
                null,
                List.of("test-region-1", "test-region-2"),
                TEST_ENVIRONMENT_ID
        );
        underTest = createUnderTest(List.of(profile));
        ResourceGroupsTaggingApiResource resource1 = createResource(1);
        ResourceGroupsTaggingApiResource resource2 = createResource(2);
        ResourceGroupsTaggingApiResource resource3 = createResource(3);
        ResourceGroupsTaggingApiResource resource4 = createResource(4);
        when(fetcher.getResources(new AwsProfileAndRegion(profile, "test-region-1"))).thenReturn(List.of(
                resource1,
                resource2
        ));
        when(fetcher.getResources(new AwsProfileAndRegion(profile, "test-region-2"))).thenReturn(List.of(
                resource3,
                resource4
        ));
        Component component1 = createComponent(1);
        Component component2 = createComponent(2);
        Component component3 = createComponent(3);
        Component component4 = createComponent(4);
        when(mapper.mapResourcesToComponents(profile.getEnvironmentId(), List.of(
                resource1,
                resource2
        ))).thenReturn(List.of(
                component1,
                component2
        ));
        when(mapper.mapResourcesToComponents(profile.getEnvironmentId(), List.of(
                resource3,
                resource4
        ))).thenReturn(List.of(
                component3,
                component4
        ));

        // When
        List<Component> returnValue = underTest.getComponents();

        // Then
        assertThat(returnValue).containsExactly(
                component1,
                component2,
                component3,
                component4
        );
    }

    @Test
    public void getDependenciesShouldFetchResourcesForMultipleProfilesAndMultipleRegionsAndMapResourcesToComponents() {
        // Given
        AwsProfileConfig profile1 = new AwsProfileConfig(
                null,
                null,
                List.of("test-region-1", "test-region-2"),
                "test-environment-id-1"
        );
        AwsProfileConfig profile2 = new AwsProfileConfig(
                null,
                null,
                List.of("test-region-3", "test-region-4"),
                "test-environment-id-2"
        );
        underTest = createUnderTest(List.of(profile1, profile2));
        ResourceGroupsTaggingApiResource resource1 = createResource(1);
        ResourceGroupsTaggingApiResource resource2 = createResource(2);
        ResourceGroupsTaggingApiResource resource3 = createResource(3);
        ResourceGroupsTaggingApiResource resource4 = createResource(4);
        ResourceGroupsTaggingApiResource resource5 = createResource(5);
        ResourceGroupsTaggingApiResource resource6 = createResource(6);
        ResourceGroupsTaggingApiResource resource7 = createResource(7);
        ResourceGroupsTaggingApiResource resource8 = createResource(8);
        when(fetcher.getResources(new AwsProfileAndRegion(profile1, "test-region-1"))).thenReturn(List.of(
                resource1,
                resource2
        ));
        when(fetcher.getResources(new AwsProfileAndRegion(profile1, "test-region-2"))).thenReturn(List.of(
                resource3,
                resource4
        ));
        when(fetcher.getResources(new AwsProfileAndRegion(profile2, "test-region-3"))).thenReturn(List.of(
                resource5,
                resource6
        ));
        when(fetcher.getResources(new AwsProfileAndRegion(profile2, "test-region-4"))).thenReturn(List.of(
                resource7,
                resource8
        ));
        Component component1 = createComponent(1);
        Component component2 = createComponent(2);
        Component component3 = createComponent(3);
        Component component4 = createComponent(4);
        Component component5 = createComponent(5);
        Component component6 = createComponent(6);
        Component component7 = createComponent(7);
        Component component8 = createComponent(8);
        when(mapper.mapResourcesToComponents(profile1.getEnvironmentId(), List.of(
                resource1,
                resource2
        ))).thenReturn(List.of(
                component1,
                component2
        ));
        when(mapper.mapResourcesToComponents(profile1.getEnvironmentId(), List.of(
                resource3,
                resource4
        ))).thenReturn(List.of(
                component3,
                component4
        ));
        when(mapper.mapResourcesToComponents(profile2.getEnvironmentId(), List.of(
                resource5,
                resource6
        ))).thenReturn(List.of(
                component5,
                component6
        ));
        when(mapper.mapResourcesToComponents(profile2.getEnvironmentId(), List.of(
                resource7,
                resource8
        ))).thenReturn(List.of(
                component7,
                component8
        ));

        // When
        List<Component> returnValue = underTest.getComponents();

        // Then
        assertThat(returnValue).containsExactly(
                component1,
                component2,
                component3,
                component4,
                component5,
                component6,
                component7,
                component8
        );
    }

    private Component createComponent(int componentNumber) {
        return Component.builder()
                .id("test-component-id-" + componentNumber)
                .build();
    }

    private ResourceService createUnderTest(List<AwsProfileConfig> profiles) {
        return new ResourceService(
                fetcher,
                mapper,
                new AwsConfig(
                        profiles,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );
    }
}
