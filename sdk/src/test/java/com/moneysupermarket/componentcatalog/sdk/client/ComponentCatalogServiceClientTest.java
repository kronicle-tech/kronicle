package com.moneysupermarket.componentcatalog.sdk.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.moneysupermarket.componentcatalog.sdk.models.Component;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

public class ComponentCatalogServiceClientTest {

    private ComponentCatalogServiceClient underTest;
    private WireMockServer wireMockServer;

    @BeforeEach
    public void beforeEach() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.stubFor(get(urlPathEqualTo("/v1/components"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"components\":[{\"id\":\"test-id\"}]}")));
        wireMockServer.start();
        underTest = new ComponentCatalogServiceClient(WebClient.create(), wireMockServer.baseUrl());
    }

    @AfterEach
    public void afterEach() {
        wireMockServer.stop();
        wireMockServer = null;
    }

    @Test
    public void getComponentsShouldReturnAListOfComponents() {
        // When
        List<Component> returnValue = underTest.getComponents();

        // Then
        assertThat(returnValue).containsExactly(Component.builder().id("test-id").build());
    }
}
