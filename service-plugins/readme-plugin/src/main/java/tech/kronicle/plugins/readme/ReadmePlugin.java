package tech.kronicle.plugins.readme;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.readme.guice.GuiceModule;

import java.util.List;

public class ReadmePlugin extends KronicleGuicePlugin {

    public static final String ID = "readme";

    public ReadmePlugin(PluginWrapper wrapper) {
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
