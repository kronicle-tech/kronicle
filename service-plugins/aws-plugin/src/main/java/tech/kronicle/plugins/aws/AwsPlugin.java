package tech.kronicle.plugins.aws;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.guice.GuiceModule;

import java.util.List;

public class AwsPlugin extends KronicleGuicePlugin {

    public static final String ID = "aws";

    public AwsPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return AwsConfig.class;
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }
}
