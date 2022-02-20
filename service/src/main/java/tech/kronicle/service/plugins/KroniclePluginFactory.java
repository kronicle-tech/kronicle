package tech.kronicle.service.plugins;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.pf4j.DefaultPluginFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class KroniclePluginFactory extends DefaultPluginFactory {

    private final ConfigurableApplicationContext applicationContext;

    @SneakyThrows
    @Override
    public Plugin create(PluginWrapper pluginWrapper) {
        Plugin plugin = super.create(pluginWrapper);

        if (nonNull(plugin) && plugin instanceof SpringPlugin) {
            SpringPlugin springPlugin = (SpringPlugin) plugin;

            if (springPlugin.getApplicationContext() instanceof ConfigurableApplicationContext) {
                ConfigurableApplicationContext pluginApplicationContext = (ConfigurableApplicationContext) springPlugin.getApplicationContext();

                Class<?> pluginClass = plugin.getClass();
                List<Method> methods = Arrays.asList(pluginClass.getMethods());

                if (methods.stream().anyMatch(it -> Objects.equals(it.getName(), "getConfigType"))) {
                    Method getConfigTypeMethod = pluginClass.getMethod("getConfigType");
                    Class<?> configType = (Class<?>) getConfigTypeMethod.invoke(plugin);

                    Binder binder = Binder.get(applicationContext.getEnvironment());
                    Object config = binder.bind("plugins." + pluginWrapper.getPluginId(), Bindable.of(configType)).get();

                    ConfigurableListableBeanFactory pluginBeanFactory = pluginApplicationContext.getBeanFactory();
                    pluginBeanFactory.registerSingleton("pluginConfig", config);
                }

                pluginApplicationContext.refresh();
            }
        }

        return plugin;
    }
}