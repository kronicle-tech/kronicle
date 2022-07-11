package tech.kronicle.plugins.structurediagram;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.DiagramFinder;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.structurediagram.services.StructureDiagramCreator;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Diagram;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class StructureDiagramFinder extends DiagramFinder {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    private final StructureDiagramCreator structureDiagramCreator;

    @Override
    public String description() {
        return "Creates structure diagrams from the connections on components";
    }

    @Override
    public Output<List<Diagram>, Void> find(ComponentMetadata input) {
        return Output.ofOutput(structureDiagramCreator.createStructureDiagrams(input), CACHE_TTL);
    }
}
