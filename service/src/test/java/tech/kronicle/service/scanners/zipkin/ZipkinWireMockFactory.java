package tech.kronicle.service.scanners.zipkin;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static tech.kronicle.service.testutils.TestFileHelper.readTestFile;

public class ZipkinWireMockFactory {

    private static final long LOOKBACK = TimeUnit.DAYS.toMillis(1);

    public WireMockServer createWithRealResponses(int port) {
        return createWithRealResponses(port, UnaryOperator.identity());
    }

    public WireMockServer createWithErrorResponses(int port) {
        return create(port, wireMockServer -> {
            wireMockServer.stubFor(get(urlPathEqualTo("/zipkin/api/v2/dependencies"))
                    .willReturn(aResponse().withStatus(500)));
            wireMockServer.stubFor(get(urlPathEqualTo("/zipkin/api/v2/services"))
                    .willReturn(aResponse().withStatus(500)));
            wireMockServer.stubFor(get(urlPathEqualTo("/zipkin/api/v2/spans"))
                    .willReturn(aResponse().withStatus(500)));
            wireMockServer.stubFor(get(urlPathEqualTo("/zipkin/api/v2/traces"))
                    .willReturn(aResponse().withStatus(500)));
        });
    }

    public WireMockServer createWithAuthCookie(int port) {
        return createWithRealResponses(port, mappingBuilder -> mappingBuilder.withHeader("Cookie", equalTo("test-name=test-value")));
    }

    public WireMockServer createWithAuthCookieWithSpecialCharacters(int port) {
        return createWithRealResponses(port, mappingBuilder -> mappingBuilder.withHeader("Cookie", equalTo("test-name%25=test-value%25")));
    }

    private WireMockServer createWithRealResponses(int port, UnaryOperator<MappingBuilder> enhancer) {
        return create(port, wireMockServer -> {
            wireMockServer.stubFor(enhancer.apply(get(urlPathEqualTo("/zipkin/api/v2/dependencies"))
                    .withQueryParam("endTs", matching("[1-9][0-9]*"))
                    .withQueryParam("lookback", equalTo(Long.toString(LOOKBACK))))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(readTestFile("zipkin-api-responses/dependencies.json", ZipkinWireMockFactory.class))));
            wireMockServer.stubFor(enhancer.apply(get(urlPathEqualTo("/zipkin/api/v2/services")))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(readTestFile("zipkin-api-responses/services.json", ZipkinWireMockFactory.class))));
            List.of("test-service-1", "test-service-2").forEach(serviceName -> {
                wireMockServer.stubFor(enhancer.apply(get(urlPathEqualTo("/zipkin/api/v2/spans"))
                        .withQueryParam("serviceName", equalTo(serviceName)))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(readTestFile(String.format("zipkin-api-responses/spans_%s.json", serviceName), ZipkinWireMockFactory.class))));
                List.of("-span-1", "-span-2").stream().map(suffix -> serviceName + suffix).forEach(spanName ->
                        wireMockServer.stubFor(enhancer.apply(get(urlPathEqualTo("/zipkin/api/v2/traces"))
                                .withQueryParam("serviceName", equalTo(serviceName))
                                .withQueryParam("spanName", equalTo(spanName))
                                .withQueryParam("endTs", matching("[1-9][0-9]*"))
                                .withQueryParam("lookback", equalTo(Long.toString(LOOKBACK)))
                                .withQueryParam("limit", equalTo("100")))
                                .willReturn(aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(readTestFile(String.format("zipkin-api-responses/traces_%s_%s.json", serviceName, spanName), ZipkinWireMockFactory.class)))));
            });
        });
    }

    private WireMockServer create(int port, Consumer<WireMockServer> initializer) {
        WireMockServer wireMockServer = new WireMockServer(port);
        initializer.accept(wireMockServer);
        wireMockServer.start();
        return wireMockServer;
    }
}
