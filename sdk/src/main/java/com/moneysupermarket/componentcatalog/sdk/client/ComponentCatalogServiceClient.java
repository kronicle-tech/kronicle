package com.moneysupermarket.componentcatalog.sdk.client;

import com.moneysupermarket.componentcatalog.sdk.constants.ServiceUrlPaths;
import com.moneysupermarket.componentcatalog.sdk.models.Component;
import com.moneysupermarket.componentcatalog.sdk.models.GetComponentsResponse;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

public class ComponentCatalogServiceClient {

    private static final Duration TIMEOUT = Duration.ofMinutes(2);

    private final WebClient webClient;
    private final String baseUrl;

    public ComponentCatalogServiceClient(WebClient webClient, String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = removeTrailingSlash(baseUrl);
    }

    private String removeTrailingSlash(String baseUrl) {
        return baseUrl.replaceAll("/+$", "");
    }

    public List<Component> getComponents() {
        ClientResponse clientResponse = webClient.get()
                .uri(baseUrl + ServiceUrlPaths.V1_COMPONENTS)
                .exchange()
                .block(TIMEOUT);
        return clientResponse.bodyToMono(GetComponentsResponse.class).block(TIMEOUT).getComponents();
    }
}
