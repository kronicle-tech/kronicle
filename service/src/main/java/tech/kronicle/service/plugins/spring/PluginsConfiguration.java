package tech.kronicle.service.plugins.spring;

import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.kronicle.pluginapi.git.GitCloner;
import tech.kronicle.service.plugins.KroniclePluginManagerFactory;
import tech.kronicle.service.plugins.config.PluginManagerConfig;

import java.util.List;

@Configuration
public class PluginsConfiguration {

    @Bean
    public PluginManager pluginManager(
            PluginManagerConfig pluginManagerConfig,
            @Value("${info.app.version}") String version,
            ConfigurableApplicationContext applicationContext,
            KroniclePluginManagerFactory pluginManagerFactory
    ) {
        return pluginManagerFactory.create(pluginManagerConfig, version, applicationContext);
    }

    @Bean
    public GitCloner gitCloner(PluginManager pluginManager) {
        List<GitCloner> gitCloners = pluginManager.getExtensions(GitCloner.class);

        if (gitCloners.isEmpty()) {
            throw new RuntimeException("No GitCloner extension is available");
        } else if (gitCloners.size() > 1) {
            throw new RuntimeException("More than 1 GitCloner extension is available");
        }

        return gitCloners.get(0);
    }
}
