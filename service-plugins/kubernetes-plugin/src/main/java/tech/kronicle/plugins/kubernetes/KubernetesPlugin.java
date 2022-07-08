package tech.kronicle.plugins.kubernetes;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.kubernetes.config.KubernetesConfig;
import tech.kronicle.plugins.kubernetes.guice.GuiceModule;

import java.util.List;

public class KubernetesPlugin extends KronicleGuicePlugin {

    public static final String ID = "kubernetes";

    public KubernetesPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return KubernetesConfig.class;
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }
}
