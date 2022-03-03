package tech.kronicle.service.plugins;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.DefaultPluginFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;
import tech.kronicle.service.services.ValidatorService;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Slf4j
public class KroniclePluginFactory extends DefaultPluginFactory {

    private final ConfigurableEnvironment environment;

    @Override
    public Plugin create(PluginWrapper pluginWrapper) {
        Plugin plugin = super.create(pluginWrapper);

        if (isNull(plugin)) {
            return null;
        }

        Class<?> pluginClass = plugin.getClass();

        if (pluginHasConfigMethods(pluginClass)) {
            Class<?> configType = getConfigType(pluginClass, plugin);
            Object config = null;
            if (nonNull(configType)) {
                config = loadConfig(pluginWrapper, configType);
            }
            initialize(pluginClass, plugin, config);
        }

        return plugin;
    }

    private boolean pluginHasConfigMethods(Class<?> pluginClass) {
        Set<String> methodNames = getMethodNames(pluginClass);
        return methodNames.contains("getConfigType") && methodNames.contains("initialize");
    }

    private Set<String> getMethodNames(Class<?> pluginClass) {
        return Stream.of(pluginClass.getMethods()).map(Method::getName).collect(Collectors.toUnmodifiableSet());
    }

    private Object loadConfig(PluginWrapper pluginWrapper, Class<?> configType) {
        Binder binder = Binder.get(environment);
        return binder.bind("plugins." + pluginWrapper.getPluginId(), Bindable.of(configType)).get();
    }

    @SneakyThrows
    private Class<?> getConfigType(Class<?> pluginClass, Plugin plugin) {
        Method method = pluginClass.getMethod("getConfigType");
        Class<?> configType = (Class<?>) method.invoke(plugin);
        return configType;
    }

    @SneakyThrows
    private void initialize(Class<?> pluginClass, Plugin plugin, Object config) {
        Method method = pluginClass.getMethod("initialize", Object.class);
        method.invoke(plugin, config);
    }
}