package tech.kronicle.plugins.zipkin;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTag;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.zipkin.client.ZipkinClient;
import tech.kronicle.plugins.zipkin.config.ZipkinConfig;
import tech.kronicle.plugins.zipkin.services.SubComponentDependencyTagFilter;
import tech.kronicle.plugins.zipkin.services.TraceMapper;
import tech.kronicle.plugins.zipkin.services.ZipkinService;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.utils.HttpClientFactory.createHttpClient;
import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

public class ZipkinTracingDataFinderTest {

    private static final int PORT = 36206;
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final ZipkinWireMockFactory zipkinWireMockFactory = new ZipkinWireMockFactory();
    private final RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(5)
            .waitDuration(Duration.ofMillis(1))
            .build();
    private final RetryRegistry retryRegistry = RetryRegistry.custom()
            .addRetryConfig("zipkin-client", retryConfig)
            .build();
    private ZipkinTracingDataFinder underTest;
    private WireMockServer wireMockServer;

    @AfterEach
    public void afterEach() {
        if (nonNull(wireMockServer)) {
            wireMockServer.stop();
        }
    }

    @Test
    public void idShouldReturnTheIdOfTheFinder() {
        // Given
        createZipkinTracingDataFinder();

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("zipkin-tracing-data");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // Given
        createZipkinTracingDataFinder();

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Retrieves trace data from Zipkin Server instance using the Zipkin REST API");
    }

    @Test
    public void notesShouldReturnTheNotesForTheFinder() {
        // Given
        createZipkinTracingDataFinder();

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isEqualTo("The finder will retrieve all traces from Zipkin up to a configurable limit" + 
                " of traces per service.  Kronicle can than use these traces to work out:\n"
                + "\n"
                + "* All the dependencies between the services\n"
                + "* Calculate the response time percentiles for each service and span name combination");
    }

    @Test
    public void findShouldReturnTraces() {
        // Given
        wireMockServer = zipkinWireMockFactory.createWithRealResponses(PORT);
        createZipkinTracingDataFinder();
        Component component = Component.builder()
                .id("test-service-1")
                .build();
        ComponentMetadata componentMetadata = ComponentMetadata.builder().components(List.of(component)).build();

        // When
        Output<TracingData, Void> returnValue = underTest.find(componentMetadata);

        // Then
        List<GenericTrace> expectedTraces = List.of(
                createTrace(
                        createSpan("1", "1"),
                        createSpan("1b", "1b")
                ),
                createTrace(
                        createSpan("1c", "1c"),
                        createSpan("1", "1")
                ),
                createTrace(
                        createSpan("1", "2"),
                        createSpan("1b", "2b")
                ),
                createTrace(
                        createSpan("1c", "2c"),
                        createSpan("1", "2")
                ),
                createTrace(
                        createSpan("2", "1"),
                        createSpan("2b", "1b")
                ),
                createTrace(
                        createSpan("2c", "1c"),
                        createSpan("2", "1")
                ),
                createTrace(
                        createSpan("2", "2"),
                        createSpan("2b", "2b")
                ),
                createTrace(
                        createSpan("2c", "2c"),
                        createSpan("2", "2")
                )
        );
        assertThat(returnValue.getOutput().getDependencies()).isEmpty();
        assertThat(returnValue.getOutput().getTraces()).containsExactlyElementsOf(expectedTraces);
        assertThat(returnValue).isEqualTo(Output.ofOutput(
                new TracingData(List.of(), expectedTraces),
                CACHE_TTL
        ));
    }

    private GenericSpan createSpan(String serviceUniqueText, String spanUniqueText) {
        return GenericSpan.builder()
                .sourceName("test-service-" + serviceUniqueText)
                .name("test-service-" + serviceUniqueText + "-span-" + spanUniqueText)
                .subComponentTags(List.of(
                        new GenericTag("http.path_template", "/test-service-" + serviceUniqueText + "-span-" + spanUniqueText)
                ))
                .timestamp(1609459200000000L)
                .duration(123L)
                .build();
    }

    private GenericTrace createTrace(GenericSpan... spans) {
        return GenericTrace.builder().spans(List.of(spans)).build();
    }

    private void createZipkinTracingDataFinder() {
        String zipkinBaseUrl = Optional.ofNullable(wireMockServer).map(WireMockServer::baseUrl).orElse("http://localhost:" + PORT);
        ZipkinConfig config = new ZipkinConfig(true, zipkinBaseUrl, Duration.ofMinutes(2), null, null, 100);
        ZipkinService zipkinService = createZipkinService(config);
        underTest = new ZipkinTracingDataFinder(
                zipkinService,
                new TraceMapper(new SubComponentDependencyTagFilter())
        );
    }

    private ZipkinService createZipkinService(ZipkinConfig config) {
        return new ZipkinService(
                new ZipkinClient(
                    config,
                    createHttpClient(),
                    createJsonMapper(),
                    Clock.systemUTC(),
                    retryRegistry
                ),
                config
        );
    }
}
