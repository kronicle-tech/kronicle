package tech.kronicle.service.plugins;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
        Object guiceInjector = getGuiceInjectorByExtensionClass(extensionClass);

        if (isNull(guiceInjector)) {
            return null;
        }

        return (T) getExtensionFromGuiceInjector(guiceInjector, extensionClass);
    }

    private Object getGuiceInjectorByExtensionClass(final Class<?> extensionClass) {
        Plugin plugin = Optional.ofNullable(this.pluginManager.whichPlugin(extensionClass))
                .map(PluginWrapper::getPlugin)
                .orElse(null);

        if (isNull(plugin)) {
            return null;
        }

        Class<?> pluginClass = plugin.getClass();
        return getGuiceInjectorFromPlugin(plugin, pluginClass);
    }

    @SneakyThrows
    private Object getGuiceInjectorFromPlugin(Plugin plugin, Class<?> pluginClass) {
        Method method = pluginClass.getMethod("getGuiceInjector");
        return method.invoke(plugin);
    }

    @SneakyThrows
    private Object getExtensionFromGuiceInjector(Object guiceInjector, Class<?> extensionClass) {
        Class<?> guiceInjectorInterfaceClass = Arrays.stream(guiceInjector.getClass().getInterfaces())
                .filter(it -> it.getName().equals("com.google.inject.Injector"))
                .findFirst().orElse(null);
        if (isNull(guiceInjectorInterfaceClass)) {
            return null;
        }
        Method method = guiceInjectorInterfaceClass.getMethod("getInstance", Class.class);
        return method.invoke(guiceInjector, extensionClass);
    }
}
