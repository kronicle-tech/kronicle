package tech.kronicle.plugins.zipkin.client;

import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.web.reactive.function.client.WebClient;
import tech.kronicle.plugins.zipkin.config.ZipkinConfig;
import tech.kronicle.plugintestutils.testutils.LogCaptor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class BaseZipkinClientTest {

    protected ZipkinClient zipkinClient(int port, String cookieName, String cookieValue) {
        return zipkinClient(port, cookieName, cookieValue, Duration.ofSeconds(60));
    }

    protected ZipkinClient zipkinClient(int port, Duration retryWaitDuration) {
        return zipkinClient(port, "test-name", "test-value", retryWaitDuration);
    }

    private ZipkinClient zipkinClient(int port, String cookieName, String cookieValue, Duration retryWaitDuration) {
        return new ZipkinClient(
                new ZipkinConfig(
                        true,
                        "http://localhost:" + port,
                        Duration.ofSeconds(60),
                        cookieName,
                        cookieValue,
                        1000
                ),
                webClient(),
                clock(),
                retryRegistry(retryWaitDuration)
        );
    }

    private RetryRegistry retryRegistry(Duration waitDuration) {
        return RetryRegistry.custom()
                .addRetryConfig(
                        "zipkin-client",
                        RetryConfig.custom()
                                .maxAttempts(10)
                                .waitDuration(waitDuration)
                                .build()
                )
                .build();
    }

    private Clock clock() {
        return Clock.fixed(LocalDateTime.of(2021, 1, 1, 0, 0).toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    }

    private WebClient webClient() {
        return WebClient.builder().build();
    }

    protected static Stream<ZipkinClientMethod> provideZipkinClientMethods(int port) {
        return Stream.of(
                new ZipkinClientMethod(ZipkinClient::getDependencies, "http://localhost:" + port + "/zipkin/api/v2/dependencies?endTs=1609459200000&lookback=86400000"),
                new ZipkinClientMethod(ZipkinClient::getServiceNames, "http://localhost:" + port + "/zipkin/api/v2/services"),
                new ZipkinClientMethod(underTest -> underTest.getSpanNames("test-service-1"), "http://localhost:" + port + "/zipkin/api/v2/spans?serviceName=test-service-1"),
                new ZipkinClientMethod(underTest -> underTest.getTraces("test-service-1", "test-service-1-span-1", 100), "http://localhost:" + port + "/zipkin/api/v2/traces?serviceName=test-service-1&spanName=test-service-1-span-1&endTs=1609459200000&lookback=86400000&limit=100"));
    }

    protected void assertLogEvents(LogCaptor logCaptor, String url) {
        List<ILoggingEvent> events = logCaptor.getEvents();
        assertThat(events).hasSize(10);
        for (int index = 0, count = events.size(); index < count; index++) {
            assertThat(events.get(index).getMessage()).isEqualTo("Call to '" + url + "' failed with status 500").as("Event %d", index);
        }
    }

    @Value
    protected static class ZipkinClientMethod {

        Function<ZipkinClient, Object> method;
        String url;

        @Override
        public String toString() {
            return url;
        }
    }
}
