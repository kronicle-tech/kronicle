package tech.kronicle.plugins.datadog.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.plugins.datadog.PluginPackage;
import tech.kronicle.plugins.datadog.config.DatadogConfig;
import tech.kronicle.plugins.datadog.dependencies.config.DatadogDependenciesConfig;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

    @Bean
    public DatadogDependenciesConfig datadogDependenciesConfig(DatadogConfig datadogConfig) {
        return datadogConfig.getDependencies();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
