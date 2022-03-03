package tech.kronicle.plugins.javaimport;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.javaimport.guice.GuiceModule;

import java.util.List;

public class JavaImportPlugin extends KronicleGuicePlugin {

    public JavaImportPlugin(PluginWrapper wrapper) {
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
