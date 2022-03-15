package tech.kronicle.plugins.aws.xray.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.aws.xray.models.XRayDependency;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DependencyAssemblerTest {

    private final DependencyAssembler underTest = new DependencyAssembler();

    @Test
    public void assembleDependenciesShouldMapXRayDependenciesToDependencies() {
        // Given
        List<XRayDependency> xRayDependencies = List.of(
                createXRayDependency(1),
                createXRayDependency(2)
        );

        // When
        List<Dependency> returnValues = underTest.assembleDependencies(xRayDependencies);

        // Then
        assertThat(returnValues).containsExactly(
                Dependency.builder()
                        .sourceComponentId("test-service-1-1")
                        .targetComponentId("test-service-1-3")
                        .build(),
                Dependency.builder()
                        .sourceComponentId("test-service-2-1")
                        .targetComponentId("test-service-2-3")
                        .build()
        );
    }

    @Test
    public void assembleDependenciesShouldDeduplicateTheDependencies() {
        // Given
        List<XRayDependency> xRayDependencies = List.of(
                createXRayDependency(1),
                createXRayDependency(1)
        );

        // When
        List<Dependency> returnValues = underTest.assembleDependencies(xRayDependencies);

        // Then
        assertThat(returnValues).containsExactly(
                Dependency.builder()
                        .sourceComponentId("test-service-1-1")
                        .targetComponentId("test-service-1-3")
                        .build()
        );
    }

    private XRayDependency createXRayDependency(int xRayDependencyNumber) {
        return new XRayDependency(
                List.of(
                        createServiceName(xRayDependencyNumber, 1),
                        createServiceName(xRayDependencyNumber, 2)
                ),
                List.of(
                        createServiceName(xRayDependencyNumber, 3),
                        createServiceName(xRayDependencyNumber, 4)
                )
        );
    }

    private String createServiceName(int xRayDependencyNumber, int serviceNumber) {
        return "test-service-" + xRayDependencyNumber + "-" + serviceNumber;
    }
}
