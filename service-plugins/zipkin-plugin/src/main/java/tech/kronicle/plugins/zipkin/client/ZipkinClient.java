package tech.kronicle.plugins.zipkin.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.plugins.zipkin.config.ZipkinConfig;
import tech.kronicle.plugins.zipkin.constants.ZipkinApiPaths;
import tech.kronicle.plugins.zipkin.models.api.Span;
import tech.kronicle.utils.HttpStatuses;
import tech.kronicle.utils.UriVariablesBuilder;
import tech.kronicle.sdk.models.zipkin.ZipkinDependency;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static tech.kronicle.utils.HttpClientFactory.createHttpRequestBuilder;
import static tech.kronicle.utils.UriTemplateUtils.expandUriTemplate;

@Slf4j
public class ZipkinClient {

    private static final String RETRY_NAME = "zipkin-client";
    private static final Duration LOOKBACK = Duration.ofDays(1);

    private final ZipkinConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final Retry retry;

    @Inject
    public ZipkinClient(ZipkinConfig config, HttpClient httpClient, ObjectMapper objectMapper, Clock clock, RetryRegistry retryRegistry) {
        this.config = config;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.clock = clock;
        this.retry = retryRegistry.retry(RETRY_NAME, RETRY_NAME);
    }

    public List<ZipkinDependency> getDependencies() {
        String uriTemplate = config.getBaseUrl() + ZipkinApiPaths.DEPENDENCIES + "?endTs={endTs}&lookback={lookback}";
        Map<String, String> uriVariables = UriVariablesBuilder.builder()
                .addUriVariable("endTs", clock.millis())
                .addUriVariable("lookback", LOOKBACK.toMillis())
                .build();

        return retry.executeSupplier(() -> makeRequest(uriTemplate, uriVariables, new TypeReference<>() {}));
    }

    public List<String> getServiceNames() {
        String uriTemplate = config.getBaseUrl() + ZipkinApiPaths.SERVICES;
        Map<String, String> uriVariables = UriVariablesBuilder.builder().build();

        return retry.executeSupplier(() -> makeRequest(uriTemplate, uriVariables, new TypeReference<>() {}));
    }

    public List<String> getSpanNames(String serviceName) {
        String uriTemplate = config.getBaseUrl() + ZipkinApiPaths.SPANS + "?serviceName={serviceName}";
        Map<String, String> uriVariables = UriVariablesBuilder.builder()
                .addUriVariable("serviceName", serviceName)
                .build();

        return retry.executeSupplier(() -> makeRequest(uriTemplate, uriVariables, new TypeReference<>() {}));
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

        return retry.executeSupplier(() -> makeRequest(uriTemplate, uriVariables, new TypeReference<>() {}));
    }

    @SneakyThrows
    private <T> T makeRequest(String uriTemplate, Map<String, String> uriVariables, TypeReference<T> bodyTypeReference) {
        HttpRequest.Builder requestBuilder = createHttpRequestBuilder(config.getTimeout())
                .uri(URI.create(expandUriTemplate(uriTemplate, uriVariables)));
        String cookieValue = getCookieHeaderValue();

        if (nonNull(cookieValue)) {
            requestBuilder = requestBuilder.header(HttpHeaders.COOKIE, cookieValue);
        }

        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        checkResponseStatus(response, uriTemplate, uriVariables);
        return objectMapper.readValue(response.body(), bodyTypeReference);
    }

    private void checkResponseStatus(HttpResponse<String> response, String uriTemplate, Map<String, String> uriVariables) {
        if (response.statusCode() != HttpStatuses.OK) {
            ZipkinClientException exception = new ZipkinClientException(
                    expandUriTemplate(uriTemplate, uriVariables),
                    response.statusCode(),
                    response.body()
            );
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
