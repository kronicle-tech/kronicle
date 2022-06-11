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

        if (isNull(pluginAndGuiceInjector.guiceInjector)) {
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
        Class<?> guiceInjectorInterfaceClass = Arrays.stream(pluginAndGuiceInjector.guiceInjector.getClass().getInterfaces())
                .filter(it -> it.getName().equals("com.google.inject.Injector"))
                .findFirst().orElse(null);
        if (isNull(guiceInjectorInterfaceClass)) {
            return null;
        }
        Method method = guiceInjectorInterfaceClass.getMethod("getInstance", Class.class);
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(pluginAndGuiceInjector.plugin.getWrapper().getPluginClassLoader());
            return method.invoke(pluginAndGuiceInjector.guiceInjector, extensionClass);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    @Value
    private static class PluginAndGuiceInjector {

        Plugin plugin;
        Object guiceInjector;
    }
}
