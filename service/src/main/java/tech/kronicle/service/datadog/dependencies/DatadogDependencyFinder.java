package tech.kronicle.service.datadog.dependencies;

import lombok.RequiredArgsConstructor;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.service.datadog.dependencies.client.DatadogDependencyClient;
import tech.kronicle.service.datadog.dependencies.config.DatadogDependenciesConfig;
import tech.kronicle.service.finders.DependencyFinder;
import tech.kronicle.service.spring.stereotypes.SpringComponent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringComponent
@RequiredArgsConstructor
public class DatadogDependencyFinder extends DependencyFinder {

    private final DatadogDependenciesConfig dependenciesConfig;
    private final DatadogDependencyClient client;

    @Override
    public String description() {
        return "Fetches component dependencies from Datadog.  ";
    }

    @Override
    public List<Dependency> find(ComponentMetadata componentMetadata) {
        return getEnvironments().stream()
                .map(client::getDependencies)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> getEnvironments() {
        return Optional.ofNullable(dependenciesConfig.getEnvironments()).orElse(List.of());
    }
}
