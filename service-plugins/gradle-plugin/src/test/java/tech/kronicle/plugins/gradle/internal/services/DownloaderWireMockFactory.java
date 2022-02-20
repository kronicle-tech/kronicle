package tech.kronicle.plugins.gradle.internal.services;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import org.springframework.http.HttpMethod;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class DownloaderWireMockFactory {

    public static final int PORT = 36207;

    public WireMockServer createWithNoStubs() {
        return create(wireMockServer -> {});
    }

    public WireMockServer create(Scenario scenario, HttpMethod httpMethod) {
        return create(wireMockServer -> stubResponses(wireMockServer, scenario, httpMethod));
    }

    private void stubResponses(WireMockServer wireMockServer, Scenario scenario, HttpMethod httpMethod) {
        switch (scenario) {
            case DOWNLOAD:
                stubDownloadRequest(wireMockServer, httpMethod);
                return;
            case DOWNLOAD_WITH_HEADERS:
                stubRequest(
                        wireMockServer,
                        httpMethod,
                        "/download-with-headers",
                        request -> request
                                .withHeader("test-header-1", equalTo("test-value-1"))
                                .withHeader("test-header-2", equalTo("test-value-2")),
                        response -> response
                                .withStatus(200)
                                .withHeader("Content-Type", "text/plain")
                                .withBody("test-output"));
                return;
            case DELAYED:
                stubRequest(
                        wireMockServer,
                        httpMethod,
                        "/delayed",
                        response -> response.withStatus(200)
                                .withHeader("Content-Type", "text/plain")
                                .withBody("delayed-output")
                                .withFixedDelay((int) Duration.ofMinutes(2).toMillis()));
                return;
            case NOT_FOUND:
                stubRequest(
                        wireMockServer,
                        httpMethod,
                        "/not-found",
                        response -> response.withStatus(404));
                return;
            case MOVED_PERMANENTLY:
                stubRequest(
                        wireMockServer,
                        httpMethod,
                        "/moved-permanently",
                        response -> response.withStatus(301)
                                .withHeader("Location", "http://localhost:" + PORT + "/download"));
                stubDownloadRequest(wireMockServer, httpMethod);
                return;
            case FOUND:
                stubRequest(
                        wireMockServer,
                        httpMethod,
                        "/found",
                        response -> response.withStatus(302)
                                .withHeader("Location", "http://localhost:" + PORT + "/download"));
                stubDownloadRequest(wireMockServer, httpMethod);
                return;
            case SEE_OTHER:
                stubRequest(
                        wireMockServer,
                        httpMethod,
                        "/see-other",
                        response -> response.withStatus(303)
                                .withHeader("Location", "http://localhost:" + PORT + "/download"));
                stubDownloadRequest(wireMockServer, httpMethod);
                return;
            case REDIRECT_TWO:
                stubRequest(
                        wireMockServer,
                        httpMethod,
                        "/redirect-twice",
                        response -> response.withStatus(302)
                                .withHeader("Location", "http://localhost:" + PORT + "/redirect-once"));
            case REDIRECT_ONCE:
                stubRequest(
                        wireMockServer,
                        httpMethod,
                        "/redirect-once",
                        response -> response.withStatus(302)
                                .withHeader("Location", "http://localhost:" + PORT + "/download"));
                stubDownloadRequest(wireMockServer, httpMethod);
                return;
            case REDIRECT_WITH_NO_LOCATION_HEADER:
                stubRequest(
                        wireMockServer,
                        httpMethod,
                        "/redirect-with-no-location-header",
                        response -> response.withStatus(302));
                return;
            case INTERNAL_SERVER_ERROR:
                stubRequest(
                        wireMockServer,
                        httpMethod,
                        "/internal-server-error",
                        response -> response.withStatus(500));
        }
    }

    private void stubDownloadRequest(WireMockServer wireMockServer, HttpMethod httpMethod) {
        stubRequest(
                wireMockServer,
                httpMethod,
                "/download",
                response -> response
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody("test-output"));
    }

    private void stubRequest(
            WireMockServer wireMockServer,
            HttpMethod httpMethod, String urlPath,
            UnaryOperator<ResponseDefinitionBuilder> response
    ) {
        stubRequest(
                wireMockServer,
                httpMethod,
                urlPath,
                UnaryOperator.identity(),
                response
        );
    }

    private void stubRequest(
            WireMockServer wireMockServer,
            HttpMethod httpMethod,
            String urlPath,
            UnaryOperator<MappingBuilder> request,
            UnaryOperator<ResponseDefinitionBuilder> response
    ) {
        wireMockServer.stubFor(
                request.apply(
                        getRequestBuilder(httpMethod, urlPathEqualTo(urlPath))
                )
                .willReturn(response.apply(aResponse())));
    }

    private MappingBuilder getRequestBuilder(HttpMethod httpMethod, UrlPattern urlPattern) {
        switch (httpMethod) {
            case GET:
                return get(urlPattern);
            case HEAD:
                return head(urlPattern);
            default:
                throw new IllegalArgumentException("Unexpected HTTP method " + httpMethod);
        }
    }

    private WireMockServer create(Consumer<WireMockServer> initializer) {
        WireMockServer wireMockServer = new WireMockServer(PORT);
        initializer.accept(wireMockServer);
        wireMockServer.start();
        return wireMockServer;
    }

    public enum Scenario {
        DELAYED,
        DOWNLOAD,
        DOWNLOAD_WITH_HEADERS,
        FOUND,
        INTERNAL_SERVER_ERROR,
        MOVED_PERMANENTLY,
        NOT_FOUND,
        REDIRECT_ONCE,
        REDIRECT_TWO,
        REDIRECT_WITH_NO_LOCATION_HEADER,
        SEE_OTHER
    }
}
