package tech.kronicle.plugins.git;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.git.config.GitConfig;
import tech.kronicle.plugins.git.guice.GuiceModule;

import java.util.List;

public class GitPlugin extends KronicleGuicePlugin {

    public static final String ID = "git";

    public GitPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }

    @Override
    public Class<?> getConfigType() {
        return GitConfig.class;
    }
}
