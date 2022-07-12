package tech.kronicle.plugins.kubernetes.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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
import tech.kronicle.plugins.kubernetes.models.ApiResourceItemContainerStatus;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Objects.nonNull;
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
        List<ApiResourceItemContainerStatus> containerStatuses = getContainerStatuses(apiResourceItem.getRaw());
        return new ApiResourceItem(
                metadata.getName(),
                metadata.getAnnotations(),
                containerStatuses
        );
    }

    private List<ApiResourceItemContainerStatus> getContainerStatuses(JsonObject json) {
        List<ApiResourceItemContainerStatus> containerStatuses = new ArrayList<>();
        JsonObject statusObject = getJsonObject(json, "status");

        if (nonNull(statusObject)) {
            JsonArray containerStatusesArray = getJsonArray(statusObject, "containerStatuses");
            if (nonNull(containerStatusesArray)) {
                for (JsonElement containerStatusJson: containerStatusesArray) {
                    if (containerStatusJson.isJsonObject()) {
                        JsonObject containerStatusObject = containerStatusJson.getAsJsonObject();
                        String containerName = getJsonString(containerStatusObject, "name");
                        String containerStateName = null;
                        LocalDateTime containerStateStartedAt = null;

                        JsonObject containerStateObject = getJsonObject(containerStatusObject, "state");
                        if (nonNull(containerStateObject)) {
                            List<String> containerStateNames = List.copyOf(containerStateObject.keySet());
                            if (containerStateNames.size() != 1) {
                                containerStateName = containerStateNames.get(0);
                                JsonObject containerStateSubObject = getJsonObject(containerStateObject, containerStateName);
                                if (nonNull(containerStateSubObject)) {
                                    String containerStateStartedAtString = getJsonString(containerStateSubObject, "startedAt");
                                    if (nonNull(containerStateStartedAtString)) {
                                        containerStateStartedAt = LocalDateTime.parse(containerStateStartedAtString, DateTimeFormatter.ISO_DATE_TIME);
                                    }
                                }
                            }
                        }

                        containerStatuses.add(new ApiResourceItemContainerStatus(
                                containerName,
                                containerStateName,
                                containerStateStartedAt
                        ));
                    }
                }
            }
        }

        return containerStatuses;
    }

    private JsonObject getJsonObject(JsonObject jsonObject, String memberName) {
        JsonElement jsonElement = jsonObject.get(memberName);
        if (nonNull(jsonElement) && jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        }
        return null;
    }

    private JsonArray getJsonArray(JsonObject jsonObject, String memberName) {
        JsonElement jsonElement = jsonObject.get(memberName);
        if (nonNull(jsonElement) && jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray();
        }
        return null;
    }

    private String getJsonString(JsonObject jsonObject, String memberName) {
        JsonElement jsonElement = jsonObject.get(memberName);
        if (nonNull(jsonElement) && jsonElement.isJsonPrimitive() && ((JsonPrimitive) jsonElement).isString()) {
            return jsonElement.getAsString();
        }
        return null;
    }

    protected ApiClient getApiClient(ClusterConfig cluster) {
        return clients.computeIfAbsent(cluster, apiClientFactory::createApiClient);
    }
}
