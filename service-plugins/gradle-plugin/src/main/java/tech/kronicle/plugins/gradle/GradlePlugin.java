package tech.kronicle.plugins.gradle;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.gradle.guice.GuiceModule;

import java.util.List;

public class GradlePlugin extends KronicleGuicePlugin {

    public static final String ID = "gradle";

    public GradlePlugin(PluginWrapper wrapper) {
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
