package tech.kronicle.plugins.aws;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.TracingDataFinder;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.plugins.aws.xray.services.DependencyService;
import tech.kronicle.sdk.models.ComponentMetadata;

import javax.inject.Inject;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AwsXrayTracingDataFinder extends TracingDataFinder {

    private final DependencyService dependencyService;

    @Override
    public String description() {
        return "Fetches component dependencies from AWS X-Ray.  ";
    }

    @Override
    public TracingData find(ComponentMetadata input) {
        return TracingData.builder()
                .dependencies(dependencyService.getDependencies())
                .build();
    }
}