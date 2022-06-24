package tech.kronicle.plugins.doc;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.doc.config.DocConfig;
import tech.kronicle.plugins.doc.guice.GuiceModule;

import java.util.List;

public class DocPlugin extends KronicleGuicePlugin {

    public static final String ID = "doc";

    public DocPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return DocConfig.class;
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }
}
