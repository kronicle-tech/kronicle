package tech.kronicle.plugins.bitbucketserver;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.bitbucketserver.config.BitbucketServerConfig;
import tech.kronicle.plugins.bitbucketserver.guice.GuiceModule;

import java.util.List;

public class BitbucketServerPlugin extends KronicleGuicePlugin {

    public BitbucketServerPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return BitbucketServerConfig.class;
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }
}
