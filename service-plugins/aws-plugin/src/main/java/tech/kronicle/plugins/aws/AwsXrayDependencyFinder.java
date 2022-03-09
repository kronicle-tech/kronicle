package tech.kronicle.plugins.aws;

import lombok.RequiredArgsConstructor;
import tech.kronicle.pluginapi.finders.DependencyFinder;
import tech.kronicle.plugins.aws.xray.services.DependencyService;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AwsXrayDependencyFinder extends DependencyFinder {

    private final DependencyService dependencyService;

    @Override
    public String description() {
        return "Fetches component dependencies from AWS X-Ray.  ";
    }

    @Override
    public List<Dependency> find(ComponentMetadata input) {
        return dependencyService.getDependencies();
    }
}
