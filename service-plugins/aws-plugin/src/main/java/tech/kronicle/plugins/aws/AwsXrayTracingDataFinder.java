package tech.kronicle.plugins.aws;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.TracingDataFinder;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.xray.services.DependencyService;
import tech.kronicle.sdk.models.ComponentMetadata;

import javax.inject.Inject;
import java.time.Duration;

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
    public Output<TracingData, Void> find(ComponentMetadata input) {
        if (config.getLoadXrayTraceData()) {
            return Output.ofOutput(
                    TracingData.builder()
                            .dependencies(dependencyService.getDependencies())
                            .build(),
                    CACHE_TTL
            );
        } else {
            return Output.ofOutput(
                    TracingData.EMPTY,
                    CACHE_TTL
            );
        }
    }
}
