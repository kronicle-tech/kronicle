package tech.kronicle.pluginapi;

import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;

public abstract class KroniclePlugin extends SpringPlugin {

    public KroniclePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    public abstract Class<?> getConfigType();

}
