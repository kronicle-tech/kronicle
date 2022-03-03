package tech.kronicle.pluginguice.testutils;

import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;

import java.nio.file.Path;

public class TestPluginWrapper extends PluginWrapper {

    public TestPluginWrapper(PluginManager pluginManager, PluginDescriptor descriptor, Path pluginPath, ClassLoader pluginClassLoader) {
        super(pluginManager, descriptor, pluginPath, pluginClassLoader);
    }
}
