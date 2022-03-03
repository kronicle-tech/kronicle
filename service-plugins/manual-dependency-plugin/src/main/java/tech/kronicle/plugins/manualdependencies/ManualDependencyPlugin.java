package tech.kronicle.plugins.manualdependencies;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;

import java.util.List;

public class ManualDependencyPlugin extends KronicleGuicePlugin {

    public ManualDependencyPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of();
    }

    @Override
    public Class<?> getConfigType() {
        return null;
    }
}
