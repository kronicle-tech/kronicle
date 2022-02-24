package tech.kronicle.plugins.sonarqube.client;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.plugins.sonarqube.config.SonarQubeConfig;

import java.util.List;

@EnableAutoConfiguration
@ComponentScan()
public class SonarQubeClientTestConfiguration {

    @Bean
    public SonarQubeConfig sonarQubeConfig() {
        return new SonarQubeConfig("http://localhost:36202", List.of());
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }
}
