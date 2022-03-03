package tech.kronicle.plugins.example;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.example.config.ExampleConfig;
import tech.kronicle.plugins.example.guice.GuiceModule;

import java.util.List;

public class ExamplePlugin extends KronicleGuicePlugin {

    public ExamplePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return ExampleConfig.class;
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }
}
