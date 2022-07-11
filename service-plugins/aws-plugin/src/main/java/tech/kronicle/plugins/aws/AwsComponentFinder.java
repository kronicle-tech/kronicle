package tech.kronicle.plugins.aws;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.ComponentFinder;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.services.ResourceService;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AwsComponentFinder extends ComponentFinder {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final ResourceService resourceService;

    @Override
    public String description() {
        return "Fetches components from AWS.  ";
    }

    @Override
    public Output<ComponentsAndDiagrams, Void> find(ComponentMetadata input) {
        return Output.ofOutput(
                wrapComponents(resourceService.getComponents()),
                CACHE_TTL
        );
    }

    private ComponentsAndDiagrams wrapComponents(List<Component> components) {
        return ComponentsAndDiagrams.builder()
                .components(components)
                .build();
    }
}
