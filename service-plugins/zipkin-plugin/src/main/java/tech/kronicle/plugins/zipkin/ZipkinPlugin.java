package tech.kronicle.plugins.zipkin;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.zipkin.config.ZipkinConfig;
import tech.kronicle.plugins.zipkin.guice.GuiceModule;

import java.util.List;

public class ZipkinPlugin extends KronicleGuicePlugin {

    public static final String ID = "zipkin";

    public ZipkinPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }

    @Override
    public Class<?> getConfigType() {
        return ZipkinConfig.class;
    }
}
