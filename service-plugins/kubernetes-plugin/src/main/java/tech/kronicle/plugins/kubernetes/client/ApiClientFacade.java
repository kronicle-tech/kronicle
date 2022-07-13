package tech.kronicle.plugins.kubernetes.client;

import tech.kronicle.plugins.kubernetes.config.ClusterConfig;
import tech.kronicle.plugins.kubernetes.models.ApiResource;
import tech.kronicle.plugins.kubernetes.models.ApiResourceItem;

import java.util.List;

public interface ApiClientFacade {

    List<ApiResource> getApiResources(ClusterConfig cluster);

    List<ApiResourceItem> getApiResourceItems(ClusterConfig cluster, ApiResource apiResource);

    void discardApiClients();
}
