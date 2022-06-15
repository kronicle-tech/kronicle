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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toUnmodifiableList;

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
    public Output<List<TracingData>, Void> find(ComponentMetadata componentMetadata) {
        List<Map.Entry<String, List<Dependency>>> dependenciesByEnvironment = getDependencies();
        List<TracingData> tracingDatas = dependenciesByEnvironment.stream()
                .map(entry -> {
                    String environmentId = entry.getKey();
                    return TracingData.builder()
                            .id("datadog-service-dependencies-" + environmentId)
                            .name("Datadog Service Dependencies - " + environmentId)
                            .pluginId(DatadogPlugin.ID)
                            .environmentId(environmentId)
                            .dependencies(entry.getValue())
                            .build();
                })
                .collect(toUnmodifiableList());
        return Output.ofOutput(
                tracingDatas,
                CACHE_TTL
        );
    }

    private List<Map.Entry<String, List<Dependency>>> getDependencies() {
        return getEnvironments().stream()
                .map(environment -> Map.entry(environment, client.getDependencies(environment)))
                .collect(toUnmodifiableList());
    }

    private List<String> getEnvironments() {
        return Optional.ofNullable(dependenciesConfig.getEnvironments()).orElse(List.of());
    }
}
