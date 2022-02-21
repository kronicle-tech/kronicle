package tech.kronicle.service.plugins.spring;

import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.kronicle.service.plugins.KroniclePluginManagerFactory;
import tech.kronicle.service.plugins.config.PluginsConfig;

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
