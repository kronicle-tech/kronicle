package tech.kronicle.service.plugins.spring;

import org.pf4j.PluginFactory;
import org.pf4j.PluginManager;
import org.pf4j.RuntimeMode;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.kronicle.service.plugins.KroniclePluginFactory;
import tech.kronicle.service.plugins.KroniclePluginManagerFactory;
import tech.kronicle.service.plugins.config.PluginsConfig;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class PluginsConfiguration {

    @Bean
    public PluginManager pluginManager(
            ConfigurableApplicationContext applicationContext,
            @Value("${info.app.version}") String version,
            PluginsConfig pluginsConfig,
            KroniclePluginManagerFactory pluginManagerFactory
    ) {
        return pluginManagerFactory.create(applicationContext, version, pluginsConfig);
    }

}
