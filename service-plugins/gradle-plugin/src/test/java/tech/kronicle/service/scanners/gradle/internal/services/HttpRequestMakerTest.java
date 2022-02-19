package tech.kronicle.service.scanners.gradle.internal.services;

import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import tech.kronicle.service.testutils.Timer;
import tech.kronicle.service.utils.ObjectReference;

import java.time.Duration;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class HttpRequestMakerTest {

    private final RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(5)
            .waitDuration(Duration.ofMillis(1))
            .build();
    private final RetryRegistry retryRegistry = RetryRegistry.custom()
            .addRetryConfig("http-request-maker", retryConfig)
            .build();
    private final HttpRequestMaker underTest = new HttpRequestMaker(retryRegistry);

    @BeforeEach
    public void beforeEach() {

    }

    @Test
    public void makeHttpRequestShouldRetryWhenHttpRequestThrowsAnException() {
        // Given
        ObjectReference<Integer> counter = new ObjectReference<>(0);
        Function<String, ResponseEntity<String>> httpRequest = ignored -> {
            counter.set(counter.get() + 1);
            throw new RuntimeException("test");
        };

        // When
        Timer timer = new Timer();
        Throwable thrown = catchThrowable(() -> underTest.makeHttpRequest(httpRequest, "https://example.com"));
        timer.stop();

        // Then
        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown).hasMessage("test");
        assertThat(counter.get()).isEqualTo(5);
        ensureRetriesDoNotSlowDownTestExecution(timer);
    }

    private void ensureRetriesDoNotSlowDownTestExecution(Timer timer) {
        // The retries should execute quickly due to waitDuration config being overridden via @SpringBootTest annotation on this class
        Assertions.assertThat(timer.getDurationInSeconds()).isLessThan(10);
    }
}
