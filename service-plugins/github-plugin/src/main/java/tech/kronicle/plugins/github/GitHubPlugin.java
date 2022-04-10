package tech.kronicle.plugins.github;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.github.config.GitHubConfig;
import tech.kronicle.plugins.github.guice.GuiceModule;

import java.util.List;

public class GitHubPlugin extends KronicleGuicePlugin {

    public static final String ID = "github";

    public GitHubPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }

    @Override
    public Class<?> getConfigType() {
        return GitHubConfig.class;
    }
}
