package tech.kronicle.service.plugins;

import org.pf4j.PluginManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import tech.kronicle.service.plugins.config.PluginManagerConfig;

@Component
public class KroniclePluginManagerFactory {

    public PluginManager create(
            PluginManagerConfig pluginManagerConfig,
            String version,
            ConfigurableApplicationContext applicationContext
    ) {
        return new KroniclePluginManager() {

            @Override
            public PluginManagerConfig getPluginManagerConfig() {
                return pluginManagerConfig;
            }

            @Override
            public String getSystemVersion() {
                return version;
            }

            @Override
            public ConfigurableApplicationContext getApplicationContext() {
                return applicationContext;
            }
        };
    }

}
