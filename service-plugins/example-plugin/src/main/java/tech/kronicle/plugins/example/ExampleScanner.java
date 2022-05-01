package tech.kronicle.plugins.example;

import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;

@Extension
public class ExampleScanner extends ComponentScanner {

    @Override
    public String description() {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public Output<Void, Component> scan(Component input) {
        throw new IllegalStateException("Not implemented");
    }
}
