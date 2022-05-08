package tech.kronicle.plugins.graphql;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.graphql.config.GraphQlConfig;
import tech.kronicle.plugins.graphql.guice.GuiceModule;

import java.util.List;

public class GraphQlPlugin extends KronicleGuicePlugin {

    public GraphQlPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }

    @Override
    public Class<?> getConfigType() {
        return GraphQlConfig.class;
    }
}
