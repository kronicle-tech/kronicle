package tech.kronicle.service.plugins;

import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;

public class KroniclePluginClassLoader extends PluginClassLoader {

    private static final String KRONICLE_SDK_PACKAGE_PREFIX = "tech.kronicle.sdk.";
    private static final String KRONICLE_SERVICE_PACKAGE_PREFIX = "tech.kronicle.service.";
    private static final String SPRING_FRAMEWORK_PACKAGE_PREFIX = "org.springframework.";
    private static final String SLF4J_PACKAGE_PREFIX = "org.slf4j.";

    public KroniclePluginClassLoader(PluginManager pluginManager, PluginDescriptor pluginDescriptor, ClassLoader parent) {
        super(pluginManager, pluginDescriptor, parent);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(className)) {
            if (className.startsWith(KRONICLE_SDK_PACKAGE_PREFIX) ||
                className.startsWith(KRONICLE_SERVICE_PACKAGE_PREFIX) ||
                className.startsWith(SPRING_FRAMEWORK_PACKAGE_PREFIX) ||
                className.startsWith(SLF4J_PACKAGE_PREFIX)) {
                return getParent().loadClass(className);
            }

            return super.loadClass(className);
        }
    }
}
