package tech.kronicle.plugins.sonarqube;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.sonarqube.config.SonarQubeConfig;
import tech.kronicle.plugins.sonarqube.guice.GuiceModule;

import java.util.List;

public class SonarQubePlugin extends KronicleGuicePlugin {

    public SonarQubePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }

    @Override
    public Class<?> getConfigType() {
        return SonarQubeConfig.class;
    }
}
