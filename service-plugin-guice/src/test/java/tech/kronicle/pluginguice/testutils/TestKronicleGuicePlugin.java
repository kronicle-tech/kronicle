package tech.kronicle.pluginguice.testutils;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;

import java.util.List;

public class TestKronicleGuicePlugin extends KronicleGuicePlugin {

    public TestKronicleGuicePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class<?> getConfigType() {
        return TestConfig.class;
    }

    @Override
    protected List<Module> getGuiceModules() {
        return List.of(new TestGuiceModule());
    }
}
