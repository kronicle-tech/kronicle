package tech.kronicle.plugins.base;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;

import java.util.List;

public class BasePlugin extends KronicleGuicePlugin {

    public BasePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return null;
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of();
    }
}
