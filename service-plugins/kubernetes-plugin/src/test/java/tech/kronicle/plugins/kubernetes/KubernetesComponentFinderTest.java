package tech.kronicle.plugins.kubernetes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.kubernetes.config.ClusterConfig;
import tech.kronicle.plugins.kubernetes.config.KubernetesConfig;
import tech.kronicle.plugins.kubernetes.services.ResourceFinder;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.kubernetes.testutils.ComponentUtils.createComponent;

@ExtendWith(MockitoExtension.class)
public class KubernetesComponentFinderTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    @Mock
    public ResourceFinder resourceFinder;

    @Test
    public void idShouldReturnTheIdOfTheFinder() {
        // Given
        KubernetesComponentFinder underTest = createUnderTest();

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("kubernetes-component");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // Given
        KubernetesComponentFinder underTest = createUnderTest();

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Fetches components from Kubernetes cluster(s).  ");
    }

    @Test
    public void findShouldReturnAllComponents() {
        // Given
        ClusterConfig cluster1 = createCluster(1);
        ClusterConfig cluster2 = createCluster(2);
        KubernetesComponentFinder underTest = createUnderTest(List.of(
                cluster1,
                cluster2
        ));
        Component component1 = createComponent(1);
        Component component2 = createComponent(2);
        Component component3 = createComponent(3);
        Component component4 = createComponent(4);
        when(resourceFinder.findComponents(cluster1)).thenReturn(List.of(
                component1,
                component2
        ));
        when(resourceFinder.findComponents(cluster2)).thenReturn(List.of(
                component3,
                component4
        ));

        // When
        Output<ComponentsAndDiagrams, Void> returnValue = underTest.find(ComponentMetadata.builder().build());

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(new ComponentsAndDiagrams(
                List.of(
                        component1,
                        component2,
                        component3,
                        component4
                ),
                List.of()
        ), CACHE_TTL));
    }

    private KubernetesComponentFinder createUnderTest() {
        return createUnderTest(List.of());
    }

    private KubernetesComponentFinder createUnderTest(List<ClusterConfig> clusters) {
        return new KubernetesComponentFinder(resourceFinder, new KubernetesConfig(clusters));
    }

    private ClusterConfig createCluster(int clusterNumber) {
        return new ClusterConfig("test-environment-id-" + clusterNumber, "test-kube-config-" + clusterNumber);
    }
}
