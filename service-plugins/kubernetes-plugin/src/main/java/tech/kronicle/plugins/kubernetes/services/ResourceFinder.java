package tech.kronicle.plugins.kubernetes.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.plugins.kubernetes.client.ApiClientFacade;
import tech.kronicle.plugins.kubernetes.config.ClusterConfig;
import tech.kronicle.plugins.kubernetes.models.ApiResource;
import tech.kronicle.sdk.models.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResourceFinder {

    private final ApiClientFacade clientFacade;
    private final ResourceMapper resourceMapper;

    public List<Component> findComponents(ClusterConfig cluster) {
        return clientFacade.getApiResources(cluster).stream()
                .flatMap(apiResource -> findComponents(cluster, apiResource))
                .collect(toUnmodifiableList());
    }

    private Stream<Component> findComponents(ClusterConfig cluster, ApiResource apiResource) {
        return clientFacade.getApiResourceItems(cluster, apiResource).stream()
                .map(item -> resourceMapper.mapResource(cluster, apiResource, item));
    }
}
