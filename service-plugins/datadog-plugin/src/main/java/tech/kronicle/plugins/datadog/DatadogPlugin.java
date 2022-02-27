package tech.kronicle.plugins.datadog;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.datadog.config.DatadogConfig;
import tech.kronicle.plugins.datadog.guice.GuiceModule;

import java.util.List;

public class DatadogPlugin extends KronicleGuicePlugin {

    public DatadogPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return DatadogConfig.class;
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }
}
