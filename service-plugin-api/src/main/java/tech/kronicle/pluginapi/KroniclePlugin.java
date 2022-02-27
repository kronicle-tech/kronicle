package tech.kronicle.pluginapi;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public abstract class KroniclePlugin<C> extends Plugin {

    public KroniclePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    public abstract Class<C> getConfigType();

    public abstract void initialize(Object config);
}
