package tech.kronicle.plugins.kubernetes.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.kubernetes.config.ClusterConfig;
import tech.kronicle.plugins.kubernetes.models.ApiResource;
import tech.kronicle.plugins.kubernetes.models.ApiResourceItem;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentConnection;
import tech.kronicle.sdk.models.Tag;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceMapperTest {

    private final ResourceMapper underTest = new ResourceMapper();

    @Test
    public void mapResourceShouldMapAResourceToAComponent() {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(Map.of());

        // When
        Component returnValue = underTest.mapResource(createCluster(), createApiResource(), apiResourceItem);

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .id("test-environment-id.test-kind.test-name")
                        .name("Kubernetes - test-environment-id - TestKind - Test Name")
                        .discovered(true)
                        .typeId("kubernetes.test-group.test-kind")
                        .tags(List.of(new Tag("environment", "test-environment-id")))
                        .platformId("kubernetes")
                        .build()
        );
    }

    @Test
    public void mapResourceShouldMapAppNameToComponentName() {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(Map.ofEntries(
                Map.entry("app.kubernetes.io/name", "test-app-name")
        ));

        // When
        Component returnValue = underTest.mapResource(createCluster(), createApiResource(), apiResourceItem);

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .id("test-environment-id.app.test-app-name")
                        .name("Kubernetes - test-environment-id - TestKind - Test Name")
                        .discovered(true)
                        .typeId("kubernetes.test-group.test-kind")
                        .tags(List.of(new Tag("environment", "test-environment-id")))
                        .platformId("kubernetes")
                        .build()
        );
    }

    @Test
    public void mapResourceShouldMapAppPartOfToComponentConnection() {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(Map.ofEntries(
                Map.entry("app.kubernetes.io/part-of", "test-app-name")
        ));

        // When
        Component returnValue = underTest.mapResource(createCluster(), createApiResource(), apiResourceItem);

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .id("test-environment-id.test-kind.test-name")
                        .name("Kubernetes - test-environment-id - TestKind - Test Name")
                        .discovered(true)
                        .typeId("kubernetes.test-group.test-kind")
                        .tags(List.of(new Tag("environment", "test-environment-id")))
                        .platformId("kubernetes")
                        .connections(List.of(
                                ComponentConnection.builder()
                                        .targetComponentId("test-environment-id.app.test-app-name")
                                        .type("super-component")
                                        .build()
                        ))
                        .build()
        );
    }

    private ClusterConfig createCluster() {
        return new ClusterConfig("test-environment-id", null);
    }

    private ApiResource createApiResource() {
        return new ApiResource("TestKind", "test.group", "v1", "test-kinds");
    }

    private ApiResourceItem createApiResourceItem() {
        return createApiResourceItem(Map.of());
    }

    private ApiResourceItem createApiResourceItem(Map<String, String> annotations) {
        return new ApiResourceItem("Test Name", annotations);
    }
}
