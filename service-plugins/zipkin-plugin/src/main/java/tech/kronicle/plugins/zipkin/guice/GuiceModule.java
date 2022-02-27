package tech.kronicle.plugins.zipkin.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import tech.kronicle.plugins.zipkin.client.ZipkinClientException;
import tech.kronicle.plugins.zipkin.config.ZipkinConfig;

import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;

import static tech.kronicle.pluginutils.HttpClientFactory.createHttpClient;
import static tech.kronicle.pluginutils.JsonMapperFactory.createJsonMapper;

public class GuiceModule extends AbstractModule {

    @Provides
    public HttpClient httpClient(ZipkinConfig config) {
        return createHttpClient(config.getTimeout());
    }

    @Provides
    public ObjectMapper objectMapper() {
        return createJsonMapper();
    }

    @Provides
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Provides
    public RetryRegistry retryRegistry() {
        return RetryRegistry.custom()
                .addRetryConfig("zipkin-client", RetryConfig.custom()
                        .maxAttempts(10)
                        .intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofSeconds(10), 2))
                        .retryExceptions(ZipkinClientException.class)
                        .build())
                .build();
    }
}
