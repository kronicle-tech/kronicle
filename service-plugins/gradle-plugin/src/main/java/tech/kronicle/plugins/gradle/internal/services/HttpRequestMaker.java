package tech.kronicle.plugins.gradle.internal.services;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.http.ResponseEntity;
import tech.kronicle.service.spring.stereotypes.SpringComponent;

import java.util.function.Function;

@SpringComponent
public class HttpRequestMaker {

    private static final String RETRY_NAME = "http-request-maker";

    private final Retry retry;

    public HttpRequestMaker(RetryRegistry retryRegistry) {
        retry = retryRegistry.retry(RETRY_NAME, RETRY_NAME);
    }

    public ResponseEntity<String> makeHttpRequest(Function<String, ResponseEntity<String>> httpRequest, String url) {
        return retry.executeSupplier(() -> httpRequest.apply(url));
    }
}