package tech.kronicle.service.plugins;

import org.pf4j.PluginFactory;
import org.pf4j.PluginLoader;
import org.pf4j.PluginManager;
import org.pf4j.RuntimeMode;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import tech.kronicle.service.plugins.config.PluginManagerConfig;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KroniclePluginManagerFactory {

    public PluginManager create(
            ConfigurableApplicationContext applicationContext,
            String version,
            PluginManagerConfig pluginManagerConfig
    ) {
        return new SpringPluginManager() {

            @Override
            public String getSystemVersion() {
                return version;
            }

            @Override
            public RuntimeMode getRuntimeMode() {
                return RuntimeMode.byName(pluginManagerConfig.getMode());
            }

            @Override
            public List<Path> getPluginsRoots() {
                return pluginManagerConfig.getPluginRootDirs().stream()
                        .map(Path::of)
                        .collect(Collectors.toList());
            }

            @Override
            protected PluginFactory createPluginFactory() {
                return new KroniclePluginFactory(applicationContext);
            }

            @Override
            protected PluginLoader createPluginLoader() {
                return new KronicleJarPluginLoader(this);
            }

            /**
             * Override init() method to prevent behaviour of registering extensions as beans in the main
             * applicationContext
             */
            @PostConstruct
            @Override
            public void init() {
                loadPlugins();
                startPlugins();
            }
        };
    }

}
