package tech.kronicle.plugins.kubernetes.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import tech.kronicle.plugins.kubernetes.models.ApiResourceItem;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentConnection;
import tech.kronicle.sdk.models.Tag;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.plugins.kubernetes.testutils.ApiResourceItemUtils.createApiResourceItem;
import static tech.kronicle.plugins.kubernetes.testutils.ApiResourceUtils.createApiResource;
import static tech.kronicle.plugins.kubernetes.testutils.ClusterConfigUtils.createCluster;

public class ResourceMapperTest {

    private final ResourceMapper underTest = new ResourceMapper();

    @ParameterizedTest
    @MethodSource("provideNullAndFalseBooleans")
    public void mapResourceShouldMapAResourceToAComponent(Boolean apiResourcesWithSupportedMetadataOnly) {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(Map.of());

        // When
        Component returnValue = underTest.mapResource(
                createCluster(apiResourcesWithSupportedMetadataOnly),
                createApiResource(),
                apiResourceItem
        );

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .id("test-environment-id-1.test-kind1.test-name-1")
                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1")
                        .discovered(true)
                        .typeId("kubernetes.test-group-1.test-kind1")
                        .tags(List.of(new Tag("environment", "test-environment-id-1")))
                        .platformId("kubernetes")
                        .build()
        );
    }

    @ParameterizedTest
    @MethodSource("provideNullAndFalseBooleans")
    public void mapResourceShouldMapAppNameToComponentName(Boolean apiResourcesWithSupportedMetadataOnly) {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(Map.ofEntries(
                Map.entry("app.kubernetes.io/name", "test-app-name")
        ));

        // When
        Component returnValue = underTest.mapResource(
                createCluster(apiResourcesWithSupportedMetadataOnly),
                createApiResource(),
                apiResourceItem
        );

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .id("test-environment-id-1.app.test-app-name")
                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1")
                        .discovered(true)
                        .typeId("kubernetes.test-group-1.test-kind1")
                        .tags(List.of(new Tag("environment", "test-environment-id-1")))
                        .platformId("kubernetes")
                        .build()
        );
    }

    @ParameterizedTest
    @MethodSource("provideNullAndFalseBooleans")
    public void mapResourceShouldMapAppPartOfToComponentConnection(Boolean apiResourcesWithSupportedMetadataOnly) {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(Map.ofEntries(
                Map.entry("app.kubernetes.io/part-of", "test-app-name")
        ));

        // When
        Component returnValue = underTest.mapResource(
                createCluster(apiResourcesWithSupportedMetadataOnly),
                createApiResource(),
                apiResourceItem
        );

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .id("test-environment-id-1.test-kind1.test-name-1")
                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1")
                        .discovered(true)
                        .typeId("kubernetes.test-group-1.test-kind1")
                        .tags(List.of(new Tag("environment", "test-environment-id-1")))
                        .platformId("kubernetes")
                        .connections(List.of(
                                ComponentConnection.builder()
                                        .targetComponentId("test-environment-id-1.app.test-app-name")
                                        .type("super-component")
                                        .environmentId("test-environment-id-1")
                                        .build()
                        ))
                        .build()
        );
    }

    @Test
    public void mapResourceShouldReturnNullWhenConfigIsSetToIgnoreResourcesWithoutSupportedMetadataAndResourceAnnotationsListIsNull() {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(null);

        // When
        Component returnValue = underTest.mapResource(createCluster(true), createApiResource(), apiResourceItem);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void mapResourceShouldReturnNullWhenConfigIsSetToIgnoreResourcesWithoutSupportedMetadataAndResourceAnnotationsListIsEmpty() {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(Map.ofEntries());

        // When
        Component returnValue = underTest.mapResource(createCluster(true), createApiResource(), apiResourceItem);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void mapResourceShouldReturnNullWhenConfigIsSetToIgnoreResourcesWithoutSupportedMetadataAndResourceAnnotationsListContainsAnUnsupportedAnnotation() {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(Map.ofEntries(
                Map.entry("not-supported", "test-annotation-value")
        ));

        // When
        Component returnValue = underTest.mapResource(createCluster(true), createApiResource(), apiResourceItem);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void mapResourceShouldReturnComponentWhenConfigIsSetToIgnoreResourcesWithoutSupportedMetadataAndResourceAnnotationsListContainsASupportedAnnotation() {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(Map.ofEntries(
                Map.entry("app.kubernetes.io/part-of", "test-app-name")
        ));

        // When
        Component returnValue = underTest.mapResource(createCluster(true), createApiResource(), apiResourceItem);

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .id("test-environment-id-1.test-kind1.test-name-1")
                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1")
                        .discovered(true)
                        .typeId("kubernetes.test-group-1.test-kind1")
                        .tags(List.of(new Tag("environment", "test-environment-id-1")))
                        .platformId("kubernetes")
                        .connections(List.of(
                                ComponentConnection.builder()
                                        .targetComponentId("test-environment-id-1.app.test-app-name")
                                        .type("super-component")
                                        .environmentId("test-environment-id-1")
                                        .build()
                        ))
                        .build()
        );
    }

    private static Stream<Boolean> provideNullAndFalseBooleans() {
        return Stream.of(null, false);
    }
}
