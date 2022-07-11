package tech.kronicle.plugins.structurediagram;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.structurediagram.guice.GuiceModule;

import java.util.List;

public class StructureDiagramPlugin extends KronicleGuicePlugin {

    public StructureDiagramPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return null;
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }
}
