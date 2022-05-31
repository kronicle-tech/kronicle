package tech.kronicle.plugins.linesofcode;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.linesofcode.guice.GuiceModule;

import java.util.List;

public class LinesOfCodePlugin extends KronicleGuicePlugin {

    public static final String ID = "lines-of-code";

    public LinesOfCodePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new GuiceModule());
    }

    @Override
    public Class<?> getConfigType() {
        return null;
    }
}
