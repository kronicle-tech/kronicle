package tech.kronicle.service.plugins;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import org.pf4j.ExtensionFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class KronicleExtensionFactory implements ExtensionFactory {

    private final KroniclePluginManager pluginManager;

    @Override
    public <T> T create(Class<T> extensionClass) {
        PluginAndGuiceInjector pluginAndGuiceInjector = getGuiceInjectorByExtensionClass(extensionClass);

        if (isNull(pluginAndGuiceInjector)) {
            return null;
        }

        return (T) getExtensionFromGuiceInjector(pluginAndGuiceInjector, extensionClass);
    }

    private PluginAndGuiceInjector getGuiceInjectorByExtensionClass(final Class<?> extensionClass) {
        Plugin plugin = Optional.ofNullable(this.pluginManager.whichPlugin(extensionClass))
                .map(PluginWrapper::getPlugin)
                .orElse(null);

        if (isNull(plugin)) {
            return null;
        }

        Class<?> pluginClass = plugin.getClass();
        return new PluginAndGuiceInjector(plugin, getGuiceInjectorFromPlugin(plugin, pluginClass));
    }

    @SneakyThrows
    private Object getGuiceInjectorFromPlugin(Plugin plugin, Class<?> pluginClass) {
        Method method = pluginClass.getMethod("getGuiceInjector");
        return method.invoke(plugin);
    }

    @SneakyThrows
    private Object getExtensionFromGuiceInjector(PluginAndGuiceInjector pluginAndGuiceInjector, Class<?> extensionClass) {
        Class<?> guiceInjectorInterfaceClass = getGuiceInjectorInterfaceClass(pluginAndGuiceInjector);
        if (isNull(guiceInjectorInterfaceClass)) {
            return null;
        }
        Method method = guiceInjectorInterfaceClass.getMethod("getInstance", Class.class);
        // The current thread's ContextClassLoader needs to be temporarily overwritten to avoid the following error
        // when using JAXB:
        //
        //     javax.xml.bind.JAXBContextFactory: com.sun.xml.bind.v2.JAXBContextFactory not a subtype
        //
        // The error is due to java.util.ServiceLoader using the current thread's ContextClassLoader when finding
        // an implementation of the JAXB API when it should be using the plugin's ClassLoader.
        try (AutoCloseable ignored = new TemporaryThreadContextClassLoader(pluginAndGuiceInjector.getPluginClassLoader())) {
            return method.invoke(pluginAndGuiceInjector.guiceInjector, extensionClass);
        }
    }

    private Class<?> getGuiceInjectorInterfaceClass(PluginAndGuiceInjector pluginAndGuiceInjector) {
        return Arrays.stream(pluginAndGuiceInjector.guiceInjector.getClass().getInterfaces())
                .filter(it -> it.getName().equals("com.google.inject.Injector"))
                .findFirst().orElse(null);
    }

    @Value
    private static class PluginAndGuiceInjector {

        Plugin plugin;
        Object guiceInjector;

        public ClassLoader getPluginClassLoader() {
            return plugin.getWrapper().getPluginClassLoader();
        }
    }

    private static class TemporaryThreadContextClassLoader implements AutoCloseable {

        private final ClassLoader originalContextClassLoader;

        public TemporaryThreadContextClassLoader(ClassLoader newThreadContextClassLoader) {
            originalContextClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(newThreadContextClassLoader);
        }

        @Override
        public void close() {
            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        }
    }
}
