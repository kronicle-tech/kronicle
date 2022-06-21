package tech.kronicle.plugins.doc;

import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;

import java.time.Duration;

@Extension
public class DocScanner extends ComponentScanner {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    @Override
    public String description() {
        return "Saves documentation files from a component's Git repo and serves those files via Kronicle's UI";
    }

    @Override
    public Output<Void, Component> scan(Component input) {
        return Output.empty(CACHE_TTL);
    }
}
