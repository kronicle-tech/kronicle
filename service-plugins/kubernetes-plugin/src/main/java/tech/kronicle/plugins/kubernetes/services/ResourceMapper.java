package tech.kronicle.plugins.kubernetes.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.kubernetes.KubernetesPlugin;
import tech.kronicle.plugins.kubernetes.config.ClusterConfig;
import tech.kronicle.plugins.kubernetes.constants.AnnotationKeys;
import tech.kronicle.plugins.kubernetes.constants.ComponentConnectionTypes;
import tech.kronicle.plugins.kubernetes.constants.Platforms;
import tech.kronicle.plugins.kubernetes.constants.TagKeys;
import tech.kronicle.plugins.kubernetes.models.ApiResource;
import tech.kronicle.plugins.kubernetes.models.ApiResourceItem;
import tech.kronicle.plugins.kubernetes.models.ApiResourceItemContainerStatus;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentConnection;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;
import tech.kronicle.sdk.models.Tag;

import javax.inject.Inject;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.common.CaseUtils.toKebabCase;
import static tech.kronicle.common.CaseUtils.toTitleCase;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResourceMapper {

    private final Clock clock;

    public Component mapResource(ClusterConfig cluster, ApiResource apiResource, ApiResourceItem item) {
        if (apiResourcesWithSupportedMetadataOnly(cluster) && !apiResourceItemHasSupportedMetadata(item)) {
            return null;
        }
        LocalDateTime updateTimestamp = LocalDateTime.now(clock);
        String name = mapName(cluster, apiResource, item);
        return Component.builder()
                .id(mapId(cluster.getEnvironmentId(), apiResource, item))
                .name(name)
                .discovered(true)
                .type(mapType(apiResource))
                .platformId(Platforms.KUBERNETES)
                .tags(mapTags(cluster.getEnvironmentId()))
                .connections(mapConnections(cluster.getEnvironmentId(), item))
                .states(mapStates(cluster, item, name, updateTimestamp))
                .build();
    }

    private boolean apiResourcesWithSupportedMetadataOnly(ClusterConfig cluster) {
        Boolean value = cluster.getApiResourcesWithSupportedMetadataOnly();
        return nonNull(value) ? value : false;
    }

    private boolean apiResourceItemHasSupportedMetadata(ApiResourceItem item) {
        Map<String, String> annotations = item.getAnnotations();
        if (isNull(annotations)) {
            return false;
        } else {
            return AnnotationKeys.SUPPORTED_KEYS.stream()
                    .anyMatch(annotations::containsKey);
        }
    }

    private String mapId(String environmentId, ApiResource apiResource, ApiResourceItem item) {
        String name = getAnnotation(item, AnnotationKeys.APP_KUBERNETES_IO_NAME);
        if (nonNull(name)) {
            return getAppId(environmentId, name);
        }
        return environmentId + "." + toKebabCase(apiResource.getKind()) + "." + toKebabCase(item.getName());
    }

    private String mapName(ClusterConfig cluster, ApiResource apiResource, ApiResourceItem item) {
        return joinName("Kubernetes", cluster.getEnvironmentId(), apiResource.getKind(), item.getName());
    }

    private String mapType(ApiResource apiResource) {
        return joinIdOrType("kubernetes", toKebabCase(apiResource.getGroup()), toKebabCase(apiResource.getKind()));
    }

    private String joinName(String... parts) {
        return join(parts, " - ");
    }

    private String joinIdOrType(String... parts) {
        return join(parts, ".");
    }

    private String join(String[] parts, String delimiter) {
        return Stream.of(parts)
                .filter(Objects::nonNull)
                .filter(part -> !part.isBlank())
                .collect(Collectors.joining(delimiter));
    }

    private List<ComponentConnection> mapConnections(String environmentId, ApiResourceItem item) {
        String partOf = getAnnotation(item, AnnotationKeys.APP_KUBERNETES_IO_PART_OF);
        if (nonNull(partOf)) {
            return List.of(
                    ComponentConnection.builder()
                            .targetComponentId(getAppId(environmentId, partOf))
                            .type(ComponentConnectionTypes.SUPER_COMPONENT)
                            .environmentId(environmentId)
                            .build()
            );
        }
        return List.of();
    }

    private String getAnnotation(ApiResourceItem item, String annotationKey) {
        return item.getAnnotations().get(annotationKey);
    }

    private String getAppId(String environmentId, String name) {
        return environmentId + ".app." + toKebabCase(name);
    }

    private List<Tag> mapTags(String environmentId) {
        return List.of(
                new Tag(TagKeys.ENVIRONMENT, environmentId)
        );
    }

    private List<ComponentState> mapStates(
            ClusterConfig cluster,
            ApiResourceItem item,
            String name,
            LocalDateTime updateTimestamp
    ) {
        if (!shouldCreateContainerStatusChecks(cluster)) {
            return List.of();
        }
        return item.getContainerStatuses().stream()
                .map(containerStatus -> mapState(cluster.getEnvironmentId(), name, containerStatus, updateTimestamp))
                .collect(toUnmodifiableList());
    }

    private boolean shouldCreateContainerStatusChecks(ClusterConfig cluster) {
        return nonNull(cluster.getCreateContainerStatusChecks())
                ? cluster.getCreateContainerStatusChecks()
                : true;
    }

    private CheckState mapState(
            String environmentId,
            String name,
            ApiResourceItemContainerStatus containerStatus,
            LocalDateTime updateTimestamp
    ) {
        return CheckState.builder()
                .pluginId(KubernetesPlugin.ID)
                .environmentId(environmentId)
                .name(name + " - " + containerStatus.getName())
                .status(mapStatus(containerStatus.getStateName()))
                .statusMessage(toTitleCase(containerStatus.getStateName()))
                .description("Container Status")
                .updateTimestamp(updateTimestamp)
                .build();
    }

    private ComponentStateCheckStatus mapStatus(String stateName) {
        if (isNull(stateName)) {
            return null;
        }
        switch (stateName) {
            case "waiting":
                return ComponentStateCheckStatus.PENDING;
            case "running":
                return ComponentStateCheckStatus.OK;
            case "terminated":
                return ComponentStateCheckStatus.WARNING;
            default:
                return ComponentStateCheckStatus.UNKNOWN;
        }
    }
}
