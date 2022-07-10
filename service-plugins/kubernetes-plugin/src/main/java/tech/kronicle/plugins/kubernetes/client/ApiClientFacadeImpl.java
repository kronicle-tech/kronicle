package tech.kronicle.plugins.kubernetes.client;

import io.kubernetes.client.Discovery;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.generic.KubernetesApiResponse;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesApi;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesListObject;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import tech.kronicle.plugins.kubernetes.config.ClusterConfig;
import tech.kronicle.plugins.kubernetes.models.ApiResource;
import tech.kronicle.plugins.kubernetes.models.ApiResourceItem;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ApiClientFacadeImpl implements ApiClientFacade {

    private final ApiClientFactory apiClientFactory;
    private final Map<ClusterConfig, ApiClient> clients = new HashMap<>();

    @SneakyThrows
    @Override
    public List<ApiResource> getApiResources(ClusterConfig cluster) {
        Discovery discovery = new Discovery(getApiClient(cluster));
        return discovery.findAll().stream()
                .map(apiResource -> new ApiResource(
                        apiResource.getKind(),
                        apiResource.getGroup(),
                        apiResource.getPreferredVersion(),
                        apiResource.getResourcePlural()
                ))
                .collect(toUnmodifiableList());
    }

    @Override
    public List<ApiResourceItem> getApiResourceItems(ClusterConfig cluster, ApiResource apiResource) {
        DynamicKubernetesApi dynamicKubernetesApi = new DynamicKubernetesApi(
                apiResource.getGroup(),
                apiResource.getPreferredVersion(),
                apiResource.getResourcePlural(),
                getApiClient(cluster)
        );
        KubernetesApiResponse<DynamicKubernetesListObject> response = dynamicKubernetesApi.list();
        if (response.isSuccess()) {
            return response.getObject().getItems().stream()
                    .map(this::mapApiResourceItem)
                    .collect(toUnmodifiableList());
        }
        return List.of();
    }

    private ApiResourceItem mapApiResourceItem(DynamicKubernetesObject apiResourceItem) {
        V1ObjectMeta metadata = apiResourceItem.getMetadata();
        return new ApiResourceItem(
                metadata.getName(),
                metadata.getAnnotations()
        );
    }

    protected ApiClient getApiClient(ClusterConfig cluster) {
        return clients.computeIfAbsent(cluster, apiClientFactory::createApiClient);
    }
}
