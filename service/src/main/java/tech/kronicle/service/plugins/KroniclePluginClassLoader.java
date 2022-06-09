package tech.kronicle.service.plugins;

import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;

import java.util.List;

import static java.util.Objects.nonNull;

public class KroniclePluginClassLoader extends PluginClassLoader {

    private static final List<String> PREFIXES_FOR_PARENT = List.of(
            "javax.annotation.",
            "tech.kronicle.sdk.",
            "tech.kronicle.componentmetadata.",
            "tech.kronicle.pluginapi.",
            "org.slf4j."
    );

    public KroniclePluginClassLoader(PluginManager pluginManager, PluginDescriptor pluginDescriptor, ClassLoader parent) {
        super(pluginManager, pluginDescriptor, parent);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(className)) {
            if (classNameMatchesPrefixForParent(className)) {
                return getParent().loadClass(className);
            }

            Class<?> c = super.loadClass(className);

            if (nonNull(c)) {
                return c;
            }

            return loadClassFromDependencies(className);
        }
    }

    private boolean classNameMatchesPrefixForParent(String className) {
        return PREFIXES_FOR_PARENT.stream().anyMatch(className::startsWith);
    }
}
