package com.moneysupermarket.componentcatalog.service.scanners.zipkin.client;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@EnableAutoConfiguration
@ComponentScan
public class ZipkinClientTestConfiguration {

    @Bean
    public Clock clock() {
        return Clock.fixed(LocalDateTime.of(2021, 1, 1, 0, 0).toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }
}
