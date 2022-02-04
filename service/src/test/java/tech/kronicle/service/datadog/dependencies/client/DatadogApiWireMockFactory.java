package tech.kronicle.service.datadog.dependencies.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.function.Consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class  DatadogApiWireMockFactory {

    public static final int PORT = 36210;
    public static final String API_KEY = "test-api-key";
    public static final String APPLICATION_KEY = "test-application-key";
    public static final String ENVIRONMENT = "test-environment-1";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WireMockServer create() {
        return create(this::stubResponses);
    }

    private void stubResponses(WireMockServer wireMockServer) {
        ObjectNode responseBody = objectMapper.createObjectNode();
        responseBody.putObject("test-service-1")
                .putArray("calls")
                .add("test-service-2")
                .add("test-service-3");
        responseBody.putObject("test-service-2")
                .putArray("calls");
        responseBody.putObject("test-service-3")
                .putArray("calls");
        responseBody.putObject("test-service-4")
                .putArray("calls")
                .add("test-service-5")
                .add("test-service-6");
        responseBody.putObject("test-service-5")
                .putArray("calls");
        responseBody.putObject("test-service-6")
                .putArray("calls");

        wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/service_dependencies"))
                .withQueryParam("env", equalTo(ENVIRONMENT))
                .withHeader("DD-API-KEY", equalTo(API_KEY))
                .withHeader("DD-APPLICATION-KEY", equalTo(APPLICATION_KEY))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonToString(responseBody))));
    }

    private String jsonToString(ObjectNode responseBody) {
        try {
            return objectMapper.writeValueAsString(responseBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private WireMockServer create(Consumer<WireMockServer> initializer) {
        WireMockServer wireMockServer = new WireMockServer(PORT);
        initializer.accept(wireMockServer);
        wireMockServer.start();
        return wireMockServer;
    }
}
