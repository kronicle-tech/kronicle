package tech.kronicle.plugins.kubernetes.services;

import io.kubernetes.client.Discovery;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.KubectlApiResources;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.generic.KubernetesApiResponse;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesApi;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesListObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.plugins.kubernetes.config.ClusterConfig;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Tag;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.common.CaseUtils.toKebabCase;
import static tech.kronicle.sdk.utils.ListUtils.unmodifiableUnionOfLists;

@Slf4j
public class KubernetesResourceFinder {

    public List<Component> findResources(ClusterConfig cluster) {
        List<Component> components = new ArrayList<>();
        ApiClient apiClient = createApiClient(cluster);

        Set<Discovery.APIResource> apiResources = getApiResources(apiClient);

        for (Discovery.APIResource apiResource : apiResources) {
            DynamicKubernetesApi dynamicKubernetesApi = new DynamicKubernetesApi(
                    apiResource.getGroup(),
                    apiResource.getPreferredVersion(),
                    apiResource.getResourcePlural(),
                    apiClient
            );
            KubernetesApiResponse<DynamicKubernetesListObject> response = dynamicKubernetesApi.list();
            if (response.isSuccess()) {
                for (KubernetesObject item : response.getObject().getItems()) {
                    components.add(
                            Component.builder()
                                    .id(toKebabCase(cluster.getEnvironmentId() + "." + apiResource.getKind() + "." + item.getMetadata().getName()))
                                    .name(cluster.getEnvironmentId() + " - " + apiResource.getKind() + " - " + item.getMetadata().getName())
                                    .typeId("kubernetes." + apiResource.getGroup() + "." + toKebabCase(apiResource.getKind()))
                                    .platformId("kubernetes")
                                    .tags(mapTags(item.getMetadata()))
                                    .build()
                    );
                }
            }
        }
        return components;
    }

    @SneakyThrows
    private Set<Discovery.APIResource> getApiResources(ApiClient apiClient) {
        KubectlApiResources kubectlApiResources = Kubectl.apiResources();
        kubectlApiResources.apiClient(apiClient);

        return kubectlApiResources.execute();
    }

    @SneakyThrows
    private ApiClient createApiClient(ClusterConfig cluster) {
        return ClientBuilder
                .kubeconfig(KubeConfig.loadKubeConfig(new StringReader(unescapeNewlines(cluster.getKubeConfig()))))
                .build();
    }

    private List<Tag> mapTags(V1ObjectMeta metadata) {
        return unmodifiableUnionOfLists(List.of(
                mapTags(metadata.getAnnotations()),
                mapTags(metadata.getLabels())
        ));
    }

    private List<Tag> mapTags(Map<String, String> map) {
        if (isNull(map)) {
            return List.of();
        }
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new Tag(entry.getKey(), entry.getValue()))
                .collect(toUnmodifiableList());
    }

    private String unescapeNewlines(String value) {
        return value.replace("\\n", "\n");
    }
}
