package tech.kronicle.plugins.kubernetes.client;

import io.kubernetes.client.Discovery;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.generic.KubernetesApiResponse;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesApi;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesListObject;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.plugins.kubernetes.config.ClusterConfig;
import tech.kronicle.plugins.kubernetes.models.ApiResource;
import tech.kronicle.plugins.kubernetes.models.ApiResourceItem;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ApiClientFacadeImpl implements ApiClientFacade {

    private final ApiClientFactory apiClientFactory;
    private final Map<ClusterConfig, ApiClient> clients = new HashMap<>();

    @SneakyThrows
    @Override
    public List<ApiResource> getApiResources(ClusterConfig cluster) {
        Discovery discovery = new Discovery(getApiClient(cluster));
        return getApiResources(discovery).stream()
                .map(apiResource -> new ApiResource(
                        apiResource.getKind(),
                        apiResource.getGroup(),
                        apiResource.getPreferredVersion(),
                        apiResource.getResourcePlural()
                ))
                .collect(toUnmodifiableList());
    }

    private Set<Discovery.APIResource> getApiResources(Discovery discovery) throws ApiException {
        try {
            return discovery.findAll();
        } catch (ApiException e) {
            logApiException(e);
            throw e;
        }
    }

    @Override
    public List<ApiResourceItem> getApiResourceItems(ClusterConfig cluster, ApiResource apiResource) {
        DynamicKubernetesApi dynamicKubernetesApi = new DynamicKubernetesApi(
                apiResource.getGroup(),
                apiResource.getPreferredVersion(),
                apiResource.getResourcePlural(),
                getApiClient(cluster)
        );
        try {
            KubernetesApiResponse<DynamicKubernetesListObject> response = getApiResourceItems(dynamicKubernetesApi);
            return response.getObject().getItems().stream()
                    .map(this::mapApiResourceItem)
                    .collect(toUnmodifiableList());
        } catch (ApiException e) {
            logApiException(e);
            return List.of();
        }
    }

    private KubernetesApiResponse<DynamicKubernetesListObject> getApiResourceItems(
            DynamicKubernetesApi dynamicKubernetesApi
    ) throws ApiException {
            return dynamicKubernetesApi.list().throwsApiException();
    }

    private void logApiException(ApiException e) {
        log.error(
                "Call to cluster's Kubernetes API failed. message: {}, code: {}, response body: {}",
                e.getMessage(),
                e.getCode(),
                e.getResponseBody(),
                e
        );
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
