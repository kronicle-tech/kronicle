package tech.kronicle.plugins.kubernetes.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tech.kronicle.plugins.kubernetes.models.ApiResourceItem;
import tech.kronicle.plugins.kubernetes.models.ApiResourceItemContainerStatus;
import tech.kronicle.sdk.models.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static tech.kronicle.plugins.kubernetes.testutils.ApiResourceItemUtils.createApiResourceItem;
import static tech.kronicle.plugins.kubernetes.testutils.ApiResourceUtils.createApiResource;
import static tech.kronicle.plugins.kubernetes.testutils.ClusterConfigUtils.createCluster;

public class ResourceMapperTest {

    private static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(2000, 2, 1, 0, 0, 0);
    private static final Clock CLOCK = Clock.fixed(
            FIXED_DATE_TIME.toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
    );

    private final ResourceMapper underTest = new ResourceMapper(CLOCK);

    @ParameterizedTest
    @MethodSource("provideDoNotIgnoreComponentsWithoutSupportedMetadata")
    public void mapResourceShouldMapAResourceToAComponent(Boolean apiResourcesWithSupportedMetadataOnly) {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(Map.of(), List.of());

        // When
        Component returnValue = underTest.mapResource(
                createCluster(apiResourcesWithSupportedMetadataOnly, null),
                createApiResource(),
                apiResourceItem
        );

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .id("test-environment-id-1.test-kind1.test-name-1")
                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1")
                        .discovered(true)
                        .type("kubernetes.test-group-1.test-kind1")
                        .tags(List.of(new Tag("environment", "test-environment-id-1")))
                        .platformId("kubernetes")
                        .build()
        );
    }

    @ParameterizedTest
    @MethodSource("provideDoNotIgnoreComponentsWithoutSupportedMetadata")
    public void mapResourceShouldMapAppNameToComponentName(Boolean apiResourcesWithSupportedMetadataOnly) {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(
                Map.ofEntries(
                        Map.entry("app.kubernetes.io/name", "test-app-name")
                ),
                List.of()
        );

        // When
        Component returnValue = underTest.mapResource(
                createCluster(apiResourcesWithSupportedMetadataOnly, null),
                createApiResource(),
                apiResourceItem
        );

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .id("test-environment-id-1.app.test-app-name")
                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1")
                        .discovered(true)
                        .type("kubernetes.test-group-1.test-kind1")
                        .tags(List.of(new Tag("environment", "test-environment-id-1")))
                        .platformId("kubernetes")
                        .build()
        );
    }

    @ParameterizedTest
    @MethodSource("provideDoNotIgnoreComponentsWithoutSupportedMetadata")
    public void mapResourceShouldMapAppPartOfToComponentConnection(Boolean apiResourcesWithSupportedMetadataOnly) {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(
                Map.ofEntries(
                        Map.entry("app.kubernetes.io/part-of", "test-app-name")
                ),
                List.of()
        );

        // When
        Component returnValue = underTest.mapResource(
                createCluster(apiResourcesWithSupportedMetadataOnly, null),
                createApiResource(),
                apiResourceItem
        );

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .id("test-environment-id-1.test-kind1.test-name-1")
                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1")
                        .discovered(true)
                        .type("kubernetes.test-group-1.test-kind1")
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
        ApiResourceItem apiResourceItem = createApiResourceItem(null, List.of());

        // When
        Component returnValue = underTest.mapResource(createCluster(true, null), createApiResource(), apiResourceItem);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void mapResourceShouldReturnNullWhenConfigIsSetToIgnoreResourcesWithoutSupportedMetadataAndResourceAnnotationsListIsEmpty() {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(Map.ofEntries(), List.of());

        // When
        Component returnValue = underTest.mapResource(createCluster(true, null), createApiResource(), apiResourceItem);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void mapResourceShouldReturnNullWhenConfigIsSetToIgnoreResourcesWithoutSupportedMetadataAndResourceAnnotationsListContainsAnUnsupportedAnnotation() {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(
                Map.ofEntries(
                        Map.entry("not-supported", "test-annotation-value")
                ),
                List.of()
        );

        // When
        Component returnValue = underTest.mapResource(createCluster(true, null), createApiResource(), apiResourceItem);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void mapResourceShouldReturnComponentWhenConfigIsSetToIgnoreResourcesWithoutSupportedMetadataAndResourceAnnotationsListContainsASupportedAnnotation() {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(
                Map.ofEntries(
                        Map.entry("app.kubernetes.io/part-of", "test-app-name")
                ),
                List.of()
        );

        // When
        Component returnValue = underTest.mapResource(createCluster(true, null), createApiResource(), apiResourceItem);

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .id("test-environment-id-1.test-kind1.test-name-1")
                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1")
                        .discovered(true)
                        .type("kubernetes.test-group-1.test-kind1")
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

    @ParameterizedTest
    @MethodSource("provideContainerStatuses")
    public void mapResourceShouldMapContainerStatuses(
            Boolean createContainerStatusChecks,
            List<ApiResourceItemContainerStatus> containerStatuses,
            List<CheckState> expectedChecks
    ) {
        // Given
        ApiResourceItem apiResourceItem = createApiResourceItem(
                Map.ofEntries(),
                containerStatuses
        );

        // When
        Component returnValue = underTest.mapResource(createCluster(false, createContainerStatusChecks), createApiResource(), apiResourceItem);

        // Then
        assertThat(returnValue).isEqualTo(
                Component.builder()
                        .id("test-environment-id-1.test-kind1.test-name-1")
                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1")
                        .discovered(true)
                        .type("kubernetes.test-group-1.test-kind1")
                        .tags(List.of(new Tag("environment", "test-environment-id-1")))
                        .platformId("kubernetes")
                        .states(List.copyOf(expectedChecks))
                        .build()
        );
    }

    private static Stream<Boolean> provideDoNotIgnoreComponentsWithoutSupportedMetadata() {
        return Stream.of(null, false);
    }

    private static Stream<Arguments> provideContainerStatuses() {
        return Stream.of(
                arguments(
                        true,
                        List.of(
                                ApiResourceItemContainerStatus.builder()
                                        .name("test-container-name-1")
                                        .build()
                        ),
                        List.of(
                                CheckState.builder()
                                        .pluginId("kubernetes")
                                        .environmentId("test-environment-id-1")
                                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1 - test-container-name-1")
                                        .description("Container Status")
                                        .updateTimestamp(FIXED_DATE_TIME)
                                        .build()
                        )
                ),
                arguments(
                        null,
                        List.of(
                                ApiResourceItemContainerStatus.builder()
                                        .name("test-container-name-1")
                                        .build()
                        ),
                        List.of(
                                CheckState.builder()
                                        .pluginId("kubernetes")
                                        .environmentId("test-environment-id-1")
                                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1 - test-container-name-1")
                                        .description("Container Status")
                                        .updateTimestamp(FIXED_DATE_TIME)
                                        .build()
                        )
                ),
                arguments(
                        false,
                        List.of(
                                ApiResourceItemContainerStatus.builder()
                                        .name("test-container-name-1")
                                        .build()
                        ),
                        List.of()
                ),
                arguments(
                        true,
                        List.of(
                                ApiResourceItemContainerStatus.builder()
                                        .name("test-container-name-1")
                                        .stateName("waiting")
                                        .build()
                        ),
                        List.of(
                                CheckState.builder()
                                        .pluginId("kubernetes")
                                        .environmentId("test-environment-id-1")
                                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1 - test-container-name-1")
                                        .description("Container Status")
                                        .status(ComponentStateCheckStatus.PENDING)
                                        .statusMessage("Waiting")
                                        .updateTimestamp(FIXED_DATE_TIME)
                                        .build()
                        )
                ),
                arguments(
                        true,
                        List.of(
                                ApiResourceItemContainerStatus.builder()
                                        .name("test-container-name-1")
                                        .stateName("running")
                                        .build()
                        ),
                        List.of(
                                CheckState.builder()
                                        .pluginId("kubernetes")
                                        .environmentId("test-environment-id-1")
                                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1 - test-container-name-1")
                                        .description("Container Status")
                                        .status(ComponentStateCheckStatus.OK)
                                        .statusMessage("Running")
                                        .updateTimestamp(FIXED_DATE_TIME)
                                        .build()
                        )
                ),
                arguments(
                        true,
                        List.of(
                                ApiResourceItemContainerStatus.builder()
                                        .name("test-container-name-1")
                                        .stateName("terminated")
                                        .build()
                        ),
                        List.of(
                                CheckState.builder()
                                        .pluginId("kubernetes")
                                        .environmentId("test-environment-id-1")
                                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1 - test-container-name-1")
                                        .description("Container Status")
                                        .status(ComponentStateCheckStatus.WARNING)
                                        .statusMessage("Terminated")
                                        .updateTimestamp(FIXED_DATE_TIME)
                                        .build()
                        )
                ),
                arguments(
                        true,
                        List.of(
                                ApiResourceItemContainerStatus.builder()
                                        .name("test-container-name-1")
                                        .stateName("does-not-exist")
                                        .build()
                        ),
                        List.of(
                                CheckState.builder()
                                        .pluginId("kubernetes")
                                        .environmentId("test-environment-id-1")
                                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1 - test-container-name-1")
                                        .description("Container Status")
                                        .status(ComponentStateCheckStatus.UNKNOWN)
                                        .statusMessage("Does Not Exist")
                                        .updateTimestamp(FIXED_DATE_TIME)
                                        .build()
                        )
                ),
                arguments(
                        true,
                        List.of(
                                ApiResourceItemContainerStatus.builder()
                                        .name("test-container-name-1")
                                        .stateName("running")
                                        .stateStartedAt(FIXED_DATE_TIME.minusMonths(1))
                                        .build()
                        ),
                        List.of(
                                CheckState.builder()
                                        .pluginId("kubernetes")
                                        .environmentId("test-environment-id-1")
                                        .name("Kubernetes - test-environment-id-1 - TestKind1 - Test Name 1 - test-container-name-1")
                                        .description("Container Status")
                                        .status(ComponentStateCheckStatus.OK)
                                        .statusMessage("Running")
                                        .updateTimestamp(FIXED_DATE_TIME)
                                        .build()
                        )
                )
        );
    }
}
