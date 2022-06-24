package tech.kronicle.plugins.doc;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.ComponentAndCodebaseScanner;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.doc.services.DocProcessor;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.doc.DocState;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DocScanner extends ComponentAndCodebaseScanner {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    private final DocProcessor docProcessor;

    @Override
    public String description() {
        return "Saves documentation files from a component's Git repo and serves those files via Kronicle's UI";
    }

    @Override
    public Output<Void, Component> scan(ComponentAndCodebase input) {
        List<DocState> docStates = docProcessor.processDocs(input.getCodebase().getDir(), input.getComponent().getDocs());
        if (docStates.isEmpty()) {
            return Output.empty(CACHE_TTL);
        }
        return Output.ofTransformer(
                component -> component.addStates(List.copyOf(docStates)),
                CACHE_TTL
        );
    }
}
