package tech.kronicle.service.plugins;

import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;

import java.util.List;

public class KroniclePluginClassLoader extends PluginClassLoader {

    private static final List<String> PREFIXES_FOR_PARENT = List.of(
            "javax.annotation.",
            "tech.kronicle.sdk.",
            "tech.kronicle.service.",
            "org.springframework.context.",
//            "org.springframework.boot.context.",
//            "org.springframework.validation.",
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

            return super.loadClass(className);
        }
    }

    private boolean classNameMatchesPrefixForParent(String className) {
        return PREFIXES_FOR_PARENT.stream().anyMatch(className::startsWith);
    }
}
