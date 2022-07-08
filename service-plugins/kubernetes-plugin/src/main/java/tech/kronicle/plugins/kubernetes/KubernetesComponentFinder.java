package tech.kronicle.plugins.kubernetes;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.ComponentFinder;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.kubernetes.config.KubernetesConfig;
import tech.kronicle.plugins.kubernetes.services.ResourceFinder;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class KubernetesComponentFinder extends ComponentFinder {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final ResourceFinder resourceFinder;
    private final KubernetesConfig config;

    @Override
    public String description() {
        return "Fetches components from Kubernetes cluster(s).  ";
    }

    @Override
    public Output<ComponentsAndDiagrams, Void> find(ComponentMetadata input) {
        List<Component> components = config.getClusters().stream()
                .map(resourceFinder::findComponents)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());

        return Output.ofOutput(
                new ComponentsAndDiagrams(components, List.of()),
                CACHE_TTL
        );
    }
}
