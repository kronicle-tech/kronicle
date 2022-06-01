package tech.kronicle.plugins.todo;

import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.pluginguice.KronicleGuicePlugin;
import tech.kronicle.plugins.todo.guice.GuiceModule;

import java.util.List;

public class ToDoPlugin extends KronicleGuicePlugin {

    public static final String ID = "todo";

    public ToDoPlugin(PluginWrapper wrapper) {
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
