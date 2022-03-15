package tech.kronicle.plugins.aws;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.ComponentFinder;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.services.ResourceService;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;

import javax.inject.Inject;
import java.util.List;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AwsComponentFinder extends ComponentFinder {

    private final ResourceService resourceService;

    @Override
    public String description() {
        return "Fetches components from AWS.  ";
    }

    @Override
    public List<Component> find(ComponentMetadata input) {
        return resourceService.getComponents();
    }
}
