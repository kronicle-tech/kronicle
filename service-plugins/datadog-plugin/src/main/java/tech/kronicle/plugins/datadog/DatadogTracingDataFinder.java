package tech.kronicle.plugins.datadog;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.TracingDataFinder;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.datadog.dependencies.client.DatadogDependencyClient;
import tech.kronicle.plugins.datadog.dependencies.config.DatadogDependenciesConfig;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DatadogTracingDataFinder extends TracingDataFinder {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final DatadogDependenciesConfig dependenciesConfig;
    private final DatadogDependencyClient client;

    @Override
    public String description() {
        return "Fetches component dependencies from Datadog.  ";
    }

    @Override
    public Output<TracingData, Void> find(ComponentMetadata componentMetadata) {
        return Output.ofOutput(
                TracingData.builder()
                        .dependencies(getDependencies())
                        .build(),
                CACHE_TTL
        );
    }

    private List<Dependency> getDependencies() {
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
