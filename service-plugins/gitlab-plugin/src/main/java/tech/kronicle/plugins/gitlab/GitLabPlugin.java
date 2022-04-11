package tech.kronicle.plugins.gitlab;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.guice.GuiceModule;

import java.util.List;

public class GitLabPlugin extends KronicleGuicePlugin {

    public static final String ID = "gitlab";

    public GitLabPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }

    @Override
    public Class<?> getConfigType() {
        return GitLabConfig.class;
    }
}
