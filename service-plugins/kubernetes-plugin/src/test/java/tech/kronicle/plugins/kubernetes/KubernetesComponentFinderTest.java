package tech.kronicle.plugins.kubernetes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.kubernetes.config.KubernetesConfig;
import tech.kronicle.plugins.kubernetes.services.KubernetesResourceFinder;
import tech.kronicle.sdk.models.ComponentMetadata;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class KubernetesComponentFinderTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    @Mock
    public KubernetesResourceFinder resourceFinder;
    public KubernetesComponentFinder underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new KubernetesComponentFinder(resourceFinder, new KubernetesConfig(List.of()));
    }

    @Test
    public void idShouldReturnTheIdOfTheFinder() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("kubernetes-component");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Fetches components from Kubernetes cluster(s).  ");
    }

    @Test
    public void findShouldReturnAllComponentsAndDiagrams() {
        // Given
        ComponentsAndDiagrams componentsAndDiagrams = new ComponentsAndDiagrams(
                List.of(),
                List.of()
        );

        // When
        Output<ComponentsAndDiagrams, Void> returnValue = underTest.find(ComponentMetadata.builder().build());

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(componentsAndDiagrams, CACHE_TTL));
    }
}
