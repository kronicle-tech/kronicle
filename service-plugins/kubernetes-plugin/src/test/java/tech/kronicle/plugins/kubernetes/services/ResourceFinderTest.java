package tech.kronicle.plugins.kubernetes.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.kubernetes.KubernetesComponentFinder;
import tech.kronicle.plugins.kubernetes.client.ApiClientFacade;
import tech.kronicle.plugins.kubernetes.config.ClusterConfig;
import tech.kronicle.plugins.kubernetes.models.ApiResource;
import tech.kronicle.plugins.kubernetes.models.ApiResourceItem;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.kubernetes.testutils.ApiResourceItemUtils.createApiResourceItem;
import static tech.kronicle.plugins.kubernetes.testutils.ApiResourceUtils.createApiResource;
import static tech.kronicle.plugins.kubernetes.testutils.ClusterConfigUtils.createCluster;
import static tech.kronicle.plugins.kubernetes.testutils.ComponentUtils.createComponent;

@ExtendWith(MockitoExtension.class)
public class ResourceFinderTest {

    @Mock
    private ApiClientFacade clientFacade;
    @Mock
    private ResourceMapper resourceMapper;

    @Test
    public void findComponentsShouldReturnAllComponentsForACluster() {
        // Given
        ClusterConfig cluster = createCluster();
        ResourceFinder underTest = new ResourceFinder(clientFacade, resourceMapper);
        ApiResource apiResource1 = createApiResource(1);
        ApiResource apiResource2 = createApiResource(2);
        when(clientFacade.getApiResources(cluster)).thenReturn(List.of(
                apiResource1,
                apiResource2
        ));
        ApiResourceItem apiResourceItem1 = createApiResourceItem(1);
        ApiResourceItem apiResourceItem2 = createApiResourceItem(2);
        ApiResourceItem apiResourceItem3 = createApiResourceItem(3);
        ApiResourceItem apiResourceItem4 = createApiResourceItem(4);
        when(clientFacade.getApiResourceItems(cluster, apiResource1)).thenReturn(List.of(
                apiResourceItem1,
                apiResourceItem2
        ));
        when(clientFacade.getApiResourceItems(cluster, apiResource2)).thenReturn(List.of(
                apiResourceItem3,
                apiResourceItem4
        ));
        Component component1 = createComponent(1);
        Component component2 = createComponent(2);
        Component component3 = createComponent(3);
        Component component4 = createComponent(4);
        when(resourceMapper.mapResource(cluster, apiResource1, apiResourceItem1)).thenReturn(component1);
        when(resourceMapper.mapResource(cluster, apiResource1, apiResourceItem2)).thenReturn(component2);
        when(resourceMapper.mapResource(cluster, apiResource2, apiResourceItem3)).thenReturn(component3);
        when(resourceMapper.mapResource(cluster, apiResource2, apiResourceItem4)).thenReturn(component4);

        // When
        List<Component> returnValue = underTest.findComponents(cluster);

        // Then
        assertThat(returnValue).isEqualTo(List.of(
                component1,
                component2,
                component3,
                component4
        ));
    }
}
