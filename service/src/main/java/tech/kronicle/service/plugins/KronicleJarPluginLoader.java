package tech.kronicle.service.plugins;

import org.pf4j.JarPluginLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;

import java.nio.file.Path;

public class KronicleJarPluginLoader extends JarPluginLoader {

    public KronicleJarPluginLoader(PluginManager pluginManager) {
        super(pluginManager);
    }

    @Override
    public ClassLoader loadPlugin(Path pluginPath, PluginDescriptor pluginDescriptor) {
        KroniclePluginClassLoader pluginClassLoader = new KroniclePluginClassLoader(pluginManager, pluginDescriptor, getClass().getClassLoader());
        pluginClassLoader.addFile(pluginPath.toFile());
        return pluginClassLoader;
    }
}
