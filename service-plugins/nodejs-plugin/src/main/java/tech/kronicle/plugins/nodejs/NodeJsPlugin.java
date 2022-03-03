package tech.kronicle.plugins.nodejs;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.nodejs.guice.GuiceModule;

import java.util.List;

public class NodeJsPlugin extends KronicleGuicePlugin {

    public NodeJsPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }

    @Override
    public Class<?> getConfigType() {
        return null;
    }
}
