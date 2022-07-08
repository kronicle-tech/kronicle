package tech.kronicle.plugins.kubernetes.services;

import tech.kronicle.plugins.kubernetes.config.ClusterConfig;
import tech.kronicle.plugins.kubernetes.constants.AnnotationKeys;
import tech.kronicle.plugins.kubernetes.constants.ComponentConnectionTypes;
import tech.kronicle.plugins.kubernetes.constants.Platforms;
import tech.kronicle.plugins.kubernetes.constants.TagKeys;
import tech.kronicle.plugins.kubernetes.models.ApiResource;
import tech.kronicle.plugins.kubernetes.models.ApiResourceItem;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentConnection;
import tech.kronicle.sdk.models.Tag;

import java.util.List;

import static java.util.Objects.nonNull;
import static tech.kronicle.common.CaseUtils.toKebabCase;

public class ResourceMapper {

    public Component mapResource(ClusterConfig cluster, ApiResource apiResource, ApiResourceItem item) {
        return Component.builder()
                .id(mapId(cluster.getEnvironmentId(), apiResource, item))
                .name(mapName(cluster, apiResource, item))
                .typeId(mapType(apiResource))
                .platformId(Platforms.KUBERNETES)
                .tags(mapTags(cluster.getEnvironmentId()))
                .connections(mapConnections(cluster.getEnvironmentId(), item))
                .build();
    }

    private String mapId(String environmentId, ApiResource apiResource, ApiResourceItem item) {
        String name = getAnnotation(item, AnnotationKeys.APP_KUBERNETES_IO_NAME);
        if (nonNull(name)) {
            return getAppId(environmentId, name);
        }
        return environmentId + "." + toKebabCase(apiResource.getKind()) + "." + toKebabCase(item.getName());
    }

    private String mapName(ClusterConfig cluster, ApiResource apiResource, ApiResourceItem item) {
        return cluster.getEnvironmentId() + " - " + apiResource.getKind() + " - " + item.getName();
    }

    private String mapType(ApiResource apiResource) {
        return "kubernetes." + apiResource.getGroup() + "." + toKebabCase(apiResource.getKind());
    }

    private List<ComponentConnection> mapConnections(String environmentId, ApiResourceItem item) {
        String partOf = getAnnotation(item, AnnotationKeys.APP_KUBERNETES_IO_PART_OF);
        if (nonNull(partOf)) {
            return List.of(
                    ComponentConnection.builder()
                            .targetComponentId(getAppId(environmentId, partOf))
                            .type(ComponentConnectionTypes.SUPER_COMPONENT)
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
}
