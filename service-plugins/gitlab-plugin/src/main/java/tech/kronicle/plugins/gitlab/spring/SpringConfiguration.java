package tech.kronicle.plugins.gitlab.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.plugins.gitlab.PluginPackage;

@ComponentScan(basePackageClasses = PluginPackage.class)
public class SpringConfiguration {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
