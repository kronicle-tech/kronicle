package tech.kronicle.plugins.aws.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.aws.xray.models.Alias;
import tech.kronicle.plugins.aws.xray.models.Edge;
import tech.kronicle.plugins.aws.xray.models.Service;
import tech.kronicle.plugins.aws.xray.services.DependencyAssembler;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DependencyAssemblerTest {

    private final DependencyAssembler underTest = new DependencyAssembler();

    @Test
    public void assembleDependenciesShouldMapServicesToDependencies() {
        // Given
        List<Service> services = List.of(
                createService(1),
                createService(2)
        );

        // When
        List<Dependency> returnValues = underTest.assembleDependencies(services);

        // Then
        assertThat(returnValues).containsExactly(
                Dependency.builder()
                        .sourceComponentId("test-service-1")
                        .targetComponentId("test-service-1-edge-1-alias-1-1")
                        .build(),
                Dependency.builder()
                        .sourceComponentId("test-service-1")
                        .targetComponentId("test-service-1-edge-2-alias-1-1")
                        .build(),
                Dependency.builder()
                        .sourceComponentId("test-service-2")
                        .targetComponentId("test-service-2-edge-1-alias-1-1")
                        .build(),
                Dependency.builder()
                        .sourceComponentId("test-service-2")
                        .targetComponentId("test-service-2-edge-2-alias-1-1")
                        .build()
        );
    }

    @Test
    public void assembleDependenciesShouldDeduplicateTheDependencies() {
        // Given
        List<Service> services = List.of(
                createService(1),
                createService(1)
        );

        // When
        List<Dependency> returnValues = underTest.assembleDependencies(services);

        // Then
        assertThat(returnValues).containsExactly(
                Dependency.builder()
                        .sourceComponentId("test-service-1")
                        .targetComponentId("test-service-1-edge-1-alias-1-1")
                        .build(),
                Dependency.builder()
                        .sourceComponentId("test-service-1")
                        .targetComponentId("test-service-1-edge-2-alias-1-1")
                        .build()
        );
    }

    private Service createService(int serviceNumber) {
        return new Service(
                "test-service-" + serviceNumber,
                List.of(
                        createServiceAlias(serviceNumber, 1),
                        createServiceAlias(serviceNumber, 2)
                ),
                List.of(
                        createEdge(serviceNumber, 1),
                        createEdge(serviceNumber, 2)
                )
        );
    }

    private String createServiceAlias(int serviceNumber, int aliasNumber) {
        return "test-service-" + serviceNumber + "-alias-" + aliasNumber;
    }

    private Edge createEdge(int serviceNumber, int edgeNumber) {
        return new Edge(List.of(
                createAlias(serviceNumber, edgeNumber, 1),
                createAlias(serviceNumber, edgeNumber, 2)));
    }

    private Alias createAlias(int serviceNumber, int edgeNumber, int aliasNumber) {
        return new Alias(
                createAliasText(serviceNumber, edgeNumber, aliasNumber, 1),
                List.of(
                        createAliasText(serviceNumber, edgeNumber, aliasNumber, 2),
                        createAliasText(serviceNumber, edgeNumber, aliasNumber, 3)
                )
        );
    }

    private String createAliasText(int serviceNumber, int edgeNumber, int aliasNumber, int subAliasNumber) {
        return "test-service-" + serviceNumber + "-edge-" + edgeNumber + "-alias-" + aliasNumber + "-" + subAliasNumber;
    }
}
