package tech.kronicle.plugins.keysoftware;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.keysoftware.config.KeySoftwareConfig;

import java.util.List;

public class KeySoftwarePlugin extends KronicleGuicePlugin {

    public KeySoftwarePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of();
    }

    @Override
    public Class<?> getConfigType() {
        return KeySoftwareConfig.class;
    }
}
