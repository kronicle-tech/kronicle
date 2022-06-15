package tech.kronicle.plugins.aws;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.TracingDataFinder;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.xray.services.DependencyService;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableList;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AwsXrayTracingDataFinder extends TracingDataFinder {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final DependencyService dependencyService;
    private final AwsConfig config;

    @Override
    public String description() {
        return "Fetches component dependencies from AWS X-Ray.  ";
    }

    @Override
    public Output<List<TracingData>, Void> find(ComponentMetadata input) {
        if (config.getLoadXrayTraceData()) {
            List<Map.Entry<AwsProfileAndRegion, List<Dependency>>> dependenciesByProfileAndRegion = dependencyService.getDependencies();
            List<TracingData> tracingDatas = dependenciesByProfileAndRegion.stream()
                    .map(entry -> {
                        String environmentId = entry.getKey().getProfile().getEnvironmentId();
                        return TracingData.builder()
                                .id("aws-xray-service-graph-" + environmentId)
                                .name("AWS X-Ray Service Graph - " + environmentId)
                                .pluginId(AwsPlugin.ID)
                                .environmentId(environmentId)
                                .dependencies(entry.getValue())
                                .build();
                    })
                    .collect(toUnmodifiableList());
            return Output.ofOutput(
                    tracingDatas,
                    CACHE_TTL
            );
        } else {
            return Output.ofOutput(
                    List.of(),
                    CACHE_TTL
            );
        }
    }
}
