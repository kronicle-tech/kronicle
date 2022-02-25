package tech.kronicle.plugins.zipkin.client;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.plugins.zipkin.config.ZipkinConfig;
import tech.kronicle.plugins.zipkin.constants.ZipkinApiPaths;
import tech.kronicle.plugins.zipkin.models.api.Span;
import tech.kronicle.pluginutils.services.UriVariablesBuilder;
import tech.kronicle.sdk.models.zipkin.ZipkinDependency;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static tech.kronicle.pluginutils.utils.UriTemplateUtils.expandUriTemplate;

@Component
@Slf4j
public class ZipkinClient {

    private static final String RETRY_NAME = "zipkin-client";
    private static final Duration LOOKBACK = Duration.ofDays(1);

    private final ZipkinConfig config;
    private final WebClient webClient;
    private final Clock clock;
    private final Retry retry;

    public ZipkinClient(ZipkinConfig config, WebClient webClient, Clock clock, RetryRegistry retryRegistry) {
        this.config = config;
        this.webClient = webClient;
        this.clock = clock;
        this.retry = retryRegistry.retry(RETRY_NAME, RETRY_NAME);
    }

    public List<ZipkinDependency> getDependencies() {
        String uriTemplate = config.getBaseUrl() + ZipkinApiPaths.DEPENDENCIES + "?endTs={endTs}&lookback={lookback}";
        Map<String, String> uriVariables = UriVariablesBuilder.builder()
                .addUriVariable("endTs", clock.millis())
                .addUriVariable("lookback", LOOKBACK.toMillis())
                .build();

        return retry.executeSupplier(() -> {
            ClientResponse clientResponse = makeRequest(webClient.get().uri(uriTemplate, uriVariables));
            checkResponseStatus(clientResponse, uriTemplate, uriVariables);
            return clientResponse.bodyToMono(new ParameterizedTypeReference<List<ZipkinDependency>>() { })
                    .block(config.getTimeout());
        });
    }

    public List<String> getServiceNames() {
        String uriTemplate = config.getBaseUrl() + ZipkinApiPaths.SERVICES;
        Map<String, String> uriVariables = UriVariablesBuilder.builder().build();

        return retry.executeSupplier(() -> {
            ClientResponse clientResponse = makeRequest(webClient.get().uri(uriTemplate));
            checkResponseStatus(clientResponse, uriTemplate, uriVariables);
            return clientResponse.bodyToMono(new ParameterizedTypeReference<List<String>>() {
            }).block(config.getTimeout());
        });
    }

    public List<String> getSpanNames(String serviceName) {
        String uriTemplate = config.getBaseUrl() + ZipkinApiPaths.SPANS + "?serviceName={serviceName}";
        Map<String, String> uriVariables = UriVariablesBuilder.builder()
                .addUriVariable("serviceName", serviceName)
                .build();

        return retry.executeSupplier(() -> {
            ClientResponse clientResponse = makeRequest(webClient.get().uri(uriTemplate, uriVariables));
            checkResponseStatus(clientResponse, uriTemplate, uriVariables);
            return clientResponse.bodyToMono(new ParameterizedTypeReference<List<String>>() {
            }).block(config.getTimeout());
        });
    }

    public List<List<Span>> getTraces(String serviceName, String spanName, int limit) {
        String uriTemplate = config.getBaseUrl() + ZipkinApiPaths.TRACES + "?serviceName={serviceName}&spanName={spanName}&endTs={endTs}&lookback={lookback}"
                + "&limit={limit}";
        Map<String, String> uriVariables = UriVariablesBuilder.builder()
                .addUriVariable("serviceName", serviceName)
                .addUriVariable("spanName", spanName)
                .addUriVariable("endTs", clock.millis())
                .addUriVariable("lookback", LOOKBACK.toMillis())
                .addUriVariable("limit", limit)
                .build();

        return retry.executeSupplier(() -> {
            ClientResponse clientResponse = makeRequest(webClient.get().uri(uriTemplate, uriVariables));
            checkResponseStatus(clientResponse, uriTemplate, uriVariables);
            return clientResponse.bodyToMono(new ParameterizedTypeReference<List<List<Span>>>() {
            }).block(config.getTimeout());
        });
    }

    private ClientResponse makeRequest(WebClient.RequestHeadersSpec<?> requestHeadersSpec) {
        String cookieValue = getCookieHeaderValue();

        if (nonNull(cookieValue)) {
            requestHeadersSpec = requestHeadersSpec.header(HttpHeaders.COOKIE, cookieValue);
        }

        return requestHeadersSpec
                .exchange()
                .block(config.getTimeout());
    }

    private void checkResponseStatus(ClientResponse clientResponse, String uriTemplate, Map<String, String> uriVariables) {
        if (clientResponse.statusCode() != HttpStatus.OK) {
            String responseBody = clientResponse.bodyToMono(String.class).block(config.getTimeout());

            ZipkinClientException exception = new ZipkinClientException(expandUriTemplate(uriTemplate, uriVariables),
                    clientResponse.rawStatusCode(), responseBody);
            log.warn(exception.getMessage());
            throw exception;
        }
    }

    private String getCookieHeaderValue() {
        String cookieName = config.getCookieName();
        String cookieValue = config.getCookieValue();

        if (nonNull(cookieName) && nonNull(cookieValue)) {
            try {
                return URLEncoder.encode(cookieName, StandardCharsets.UTF_8.name()) + "=" + URLEncoder.encode(cookieValue, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}
