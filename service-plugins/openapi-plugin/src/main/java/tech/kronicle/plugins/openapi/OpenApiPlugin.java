package tech.kronicle.plugins.openapi;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.openapi.config.OpenApiConfig;
import tech.kronicle.plugins.openapi.guice.GuiceModule;

import java.util.List;

public class OpenApiPlugin extends KronicleGuicePlugin {

    public OpenApiPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }

    @Override
    public Class<?> getConfigType() {
        return OpenApiConfig.class;
    }
}
