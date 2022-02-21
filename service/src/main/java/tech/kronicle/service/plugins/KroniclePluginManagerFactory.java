package tech.kronicle.service.plugins;

import org.pf4j.JarPluginLoader;
import org.pf4j.PluginFactory;
import org.pf4j.PluginLoader;
import org.pf4j.PluginManager;
import org.pf4j.RuntimeMode;
import org.pf4j.spring.ExtensionsInjector;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import tech.kronicle.service.plugins.config.PluginsConfig;
import tech.kronicle.service.spring.stereotypes.SpringComponent;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@SpringComponent
public class KroniclePluginManagerFactory {

    public PluginManager create(
            ConfigurableApplicationContext applicationContext,
            String version,
            PluginsConfig pluginsConfig
    ) {
        return new SpringPluginManager() {

            @Override
            public String getSystemVersion() {
                return version;
            }

            @Override
            public RuntimeMode getRuntimeMode() {
                return RuntimeMode.byName(pluginsConfig.getMode());
            }

            @Override
            public List<Path> getPluginsRoots() {
                return pluginsConfig.getPluginRootDirs().stream()
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
