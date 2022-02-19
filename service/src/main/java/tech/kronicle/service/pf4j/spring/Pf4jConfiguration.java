package tech.kronicle.service.pf4j.spring;

import org.pf4j.ExtensionFactory;
import org.pf4j.RuntimeMode;
import org.pf4j.spring.SingletonSpringExtensionFactory;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.kronicle.service.constants.SpringBeanNames;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class Pf4jConfiguration {

    @Bean(SpringBeanNames.PLUGIN_MANAGER)
    public SpringPluginManager pluginManager(
            @Value("${info.app.version}") String version,
            @Value("${pf4j.runtime-mode}") RuntimeMode runtimeMode,
            @Value("${pf4j.plugin-root-dirs}") List<String> pluginRootDirs
    ) {
        return new SpringPluginManager() {

            @Override
            public String getSystemVersion() {
                return version;
            }

            @Override
            public RuntimeMode getRuntimeMode() {
                return runtimeMode;
            }

            @Override
            public List<Path> getPluginsRoots() {
                return pluginRootDirs.stream()
                        .map(Path::of)
                        .collect(Collectors.toList());
            }

            @Override
            protected ExtensionFactory createExtensionFactory() {
                return new SingletonSpringExtensionFactory(this);
            }

        };
    }

}
