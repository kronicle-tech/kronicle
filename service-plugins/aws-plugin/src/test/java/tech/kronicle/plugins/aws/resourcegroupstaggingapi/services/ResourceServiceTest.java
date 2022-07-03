package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ComponentAndConnection;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.sdk.models.DiagramConnection;

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
    public void getDependenciesShouldFetchResourcesForAProfileAndRegionAndMapResourcesToComponentsAndADiagram() {
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
        Component component1 = createComponent(1);
        Component component2 = createComponent(2);
        DiagramConnection connection1 = createConnection(1);
        DiagramConnection connection2 = createConnection(2);
        List<ComponentAndConnection> componentsAndConnections = List.of(
                new ComponentAndConnection(component1, connection1),
                new ComponentAndConnection(component2, connection2)
        );
        when(mapper.mapResourcesToComponentsAndConnections(profile.getEnvironmentId(), services)).thenReturn(componentsAndConnections);

        // When
        ComponentsAndDiagrams returnValue = underTest.getComponentsAndDiagrams();

        // Then
        assertThat(returnValue.getComponents()).isEqualTo(List.of(
                component1,
                component2
        ));
        assertThat(returnValue.getDiagrams()).isEqualTo(List.of(
                createDiagram(List.of(
                        connection1,
                        connection2
                ))
        ));
    }

    @Test
    public void getDependenciesShouldFetchResourcesForAProfileAndMultipleRegionsAndMapResourcesToComponentsAndDiagrams() {
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
        DiagramConnection connection1 = createConnection(1);
        DiagramConnection connection2 = createConnection(2);
        DiagramConnection connection3 = createConnection(3);
        DiagramConnection connection4 = createConnection(4);
        when(mapper.mapResourcesToComponentsAndConnections(profile.getEnvironmentId(), List.of(
                resource1,
                resource2
        ))).thenReturn(List.of(
                new ComponentAndConnection(component1, connection1),
                new ComponentAndConnection(component2, connection2)
        ));
        when(mapper.mapResourcesToComponentsAndConnections(profile.getEnvironmentId(), List.of(
                resource3,
                resource4
        ))).thenReturn(List.of(
                new ComponentAndConnection(component3, connection3),
                new ComponentAndConnection(component4, connection4)
        ));

        // When
        ComponentsAndDiagrams returnValue = underTest.getComponentsAndDiagrams();

        // Then
        assertThat(returnValue.getComponents()).containsExactly(
                component1,
                component2,
                component3,
                component4
        );
        assertThat(returnValue.getDiagrams()).isEqualTo(List.of(
                createDiagram(List.of(
                        connection1,
                        connection2,
                        connection3,
                        connection4
                ))
        ));
    }

    @Test
    public void getDependenciesShouldFetchResourcesForMultipleProfilesAndMultipleRegionsAndMapResourcesToComponents() {
        // Given
        String environmentId1 = "test-environment-id-1";
        String environmentId2 = "test-environment-id-2";
        AwsProfileConfig profile1 = new AwsProfileConfig(
                null,
                null,
                List.of("test-region-1", "test-region-2"),
                environmentId1
        );
        AwsProfileConfig profile2 = new AwsProfileConfig(
                null,
                null,
                List.of("test-region-3", "test-region-4"),
                environmentId2
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
        DiagramConnection connection1 = createConnection(1);
        DiagramConnection connection2 = createConnection(2);
        DiagramConnection connection3 = createConnection(3);
        DiagramConnection connection4 = createConnection(4);
        DiagramConnection connection5 = createConnection(5);
        DiagramConnection connection6 = createConnection(6);
        DiagramConnection connection7 = createConnection(7);
        DiagramConnection connection8 = createConnection(8);
        when(mapper.mapResourcesToComponentsAndConnections(profile1.getEnvironmentId(), List.of(
                resource1,
                resource2
        ))).thenReturn(List.of(
                new ComponentAndConnection(component1, connection1),
                new ComponentAndConnection(component2, connection2)
        ));
        when(mapper.mapResourcesToComponentsAndConnections(profile1.getEnvironmentId(), List.of(
                resource3,
                resource4
        ))).thenReturn(List.of(
                new ComponentAndConnection(component3, connection3),
                new ComponentAndConnection(component4, connection4)
        ));
        when(mapper.mapResourcesToComponentsAndConnections(profile2.getEnvironmentId(), List.of(
                resource5,
                resource6
        ))).thenReturn(List.of(
                new ComponentAndConnection(component5, connection5),
                new ComponentAndConnection(component6, connection6)
        ));
        when(mapper.mapResourcesToComponentsAndConnections(profile2.getEnvironmentId(), List.of(
                resource7,
                resource8
        ))).thenReturn(List.of(
                new ComponentAndConnection(component7, connection7),
                new ComponentAndConnection(component8, connection8)
        ));

        // When
        ComponentsAndDiagrams returnValue = underTest.getComponentsAndDiagrams();

        // Then
        assertThat(returnValue.getComponents()).containsExactly(
                component1,
                component2,
                component3,
                component4,
                component5,
                component6,
                component7,
                component8
        );
        assertThat(returnValue.getDiagrams()).isEqualTo(List.of(
                createDiagram(List.of(
                        connection1,
                        connection2,
                        connection3,
                        connection4
                ), environmentId1),
                createDiagram(List.of(
                        connection5,
                        connection6,
                        connection7,
                        connection8
                ), environmentId2)
        ));

    }

    private Component createComponent(int componentNumber) {
        return Component.builder()
                .id("test-component-id-" + componentNumber)
                .build();
    }

    private DiagramConnection createConnection(int connectionNumber) {
        return DiagramConnection.builder()
                .sourceComponentId("test-component-id-" + connectionNumber + "-parent")
                .targetComponentId("test-component-id-" + connectionNumber)
                .build();
    }

    private Diagram createDiagram(List<DiagramConnection> connections) {
        return createDiagram(connections, TEST_ENVIRONMENT_ID);
    }

    private Diagram createDiagram(List<DiagramConnection> connections, String environmentId) {
        return Diagram.builder()
                .id("aws-resources-" + environmentId)
                .name("AWS Resources - " + environmentId)
                .description("This diagram shows AWS resources that have associated components.  Any AWS " +
                        "resources that are not associated with a component will not appear in the diagram")
                .discovered(true)
                .connections(connections)
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
