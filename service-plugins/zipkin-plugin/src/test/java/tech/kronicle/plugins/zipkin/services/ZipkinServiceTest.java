package tech.kronicle.plugins.zipkin.services;

import ch.qos.logback.classic.Level;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.zipkin.client.ZipkinClient;
import tech.kronicle.plugins.zipkin.client.ZipkinClientException;
import tech.kronicle.plugins.zipkin.config.ZipkinConfig;
import tech.kronicle.plugins.zipkin.models.api.Service;
import tech.kronicle.plugins.zipkin.models.api.Span;
import tech.kronicle.testutils.LogCaptor;
import tech.kronicle.testutils.SimplifiedLogEvent;
import tech.kronicle.sdk.models.zipkin.ZipkinDependency;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ZipkinServiceTest {

    private static final int TEST_TRACE_LIMIT = 1000;

    @Mock
    private ZipkinClient client;
    private LogCaptor logCaptor;
    private ZipkinService underTest;

    @BeforeEach
    public void beforeEach() {
        ZipkinConfig config = new ZipkinConfig(null, null, null, null, null, TEST_TRACE_LIMIT);
        underTest = new ZipkinService(client, config);
        logCaptor = new LogCaptor(underTest.getClass());
    }

    @AfterEach
    public void afterEach() {
        logCaptor.close();
    }

    @Test
    public void getDependenciesShouldReturnAllDependencies() {
        // Given
        ZipkinDependency dependency1 = ZipkinDependency.builder()
                .parent("test-service-1")
                .build();
        ZipkinDependency dependency2 = ZipkinDependency.builder()
                .parent("test-service-2")
                .build();
        List<ZipkinDependency> dependencies = List.of(dependency1, dependency2);
        when(client.getDependencies()).thenReturn(dependencies);

        // When
        List<ZipkinDependency> returnValue = underTest.getDependencies();

        // Then
        assertThat(returnValue).isSameAs(dependencies);
    }

    @Test
    public void getServicesShouldReturnAllServicesWithSpanNames() {
        // Given
        String service1 = "test-service-1";
        String service2 = "test-service-2";
        when(client.getServiceNames()).thenReturn(List.of(service1, service2));
        String service1Span1 = "test-service-1-span-1";
        String service1Span2 = "test-service-1-span-2";
        when(client.getSpanNames(service1)).thenReturn(List.of(service1Span1, service1Span2));
        String service2Span1 = "test-service-2-span-1";
        String service2Span2 = "test-service-2-span-2";
        when(client.getSpanNames(service2)).thenReturn(List.of(service2Span1, service2Span2));

        // When
        List<Service> returnValue = underTest.getServices();

        // Then
        assertThat(returnValue).containsExactly(
                new Service(service1, List.of(service1Span1, service1Span2)),
                new Service(service2, List.of(service2Span1, service2Span2)));
    }

    @Test
    public void getServicesShouldReturnLimitSpanNamesPerService() {
        // Given
        String service1 = "test-service-1";
        String service2 = "test-service-2";
        when(client.getServiceNames()).thenReturn(List.of(service1, service2));
        List<String> service1Spans = IntStream.range(1, 202)
                .mapToObj(number -> "test-service-1-span-" + number)
                .collect(Collectors.toList());
        when(client.getSpanNames(service1)).thenReturn(service1Spans);
        String service2Span1 = "test-service-2-span-1";
        String service2Span2 = "test-service-2-span-2";
        when(client.getSpanNames(service2)).thenReturn(List.of(service2Span1, service2Span2));

        // When
        List<Service> returnValue = underTest.getServices();

        // Then
        assertThat(returnValue).hasSize(2);
        assertThat(returnValue.get(0).getName()).isEqualTo("test-service-1");
        // For service 1, client should return 201 spans but ZipkinService should limit this to 200
        assertThat(service1Spans).hasSize(201);
        assertThat(returnValue.get(0).getSpanNames()).hasSize(200);
        assertThat(returnValue.get(0).getSpanNames()).startsWith("test-service-1-span-1");
        assertThat(returnValue.get(0).getSpanNames()).endsWith("test-service-1-span-200");
        assertThat(returnValue.get(1)).isEqualTo(new Service("test-service-2", List.of("test-service-2-span-1", "test-service-2-span-2")));
    }

    @Test
    public void getServicesShouldIgnoreAZipkinClientExceptionWhenTheRetrievingSpanNamesForAService() {
        // Given
        String service1 = "test-service-1";
        String service2 = "test-service-2";
        when(client.getServiceNames()).thenReturn(List.of(service1, service2));
        when(client.getSpanNames(service1)).thenThrow(new ZipkinClientException(null, 0, null));
        String service2Span1 = "test-service-2-span-1";
        String service2Span2 = "test-service-2-span-2";
        when(client.getSpanNames(service2)).thenReturn(List.of(service2Span1, service2Span2));

        // When
        List<Service> returnValue = underTest.getServices();

        // Then
        assertThat(returnValue).containsExactly(
                new Service(service1, List.of()),
                new Service(service2, List.of(service2Span1, service2Span2)));
        List<SimplifiedLogEvent> events = logCaptor.getSimplifiedEvents();
        assertThat(events).containsExactly(
                // Check error was logged
                new SimplifiedLogEvent(Level.ERROR, "Failed to retrieve span names for service \"test-service-1\""));
    }

    @Test
    public void getTracesShouldReturnTracesForEveryServiceAndSpanNameCombination() {
        // Given
        ServicesAndAllTraces servicesAndAllTraces = createServicesAndAllTraces();

        // When
        List<List<Span>> returnValue = underTest.getTraces(servicesAndAllTraces.services);

        // Then
        assertThat(returnValue).hasSize(8);
        returnValue.forEach(trace -> assertThat(trace).hasSize(2));
        assertThat(returnValue).containsExactlyElementsOf(servicesAndAllTraces.allTraces);
    }

    @Test
    public void getTracesShouldIgnoreAZipkinClientExceptionWhenTheRetrievingTracesForAServiceAndSpanNameCombination() {
        // Given
        ServicesAndAllTraces servicesAndAllTraces = createServicesAndAllTraces();

        Service service1 = servicesAndAllTraces.services.get(0);
        // Override expectation
        when(client.getTraces(service1.getName(), service1.getSpanNames().get(1), TEST_TRACE_LIMIT)).thenThrow(new ZipkinClientException(null, 0, null));

        // When
        List<List<Span>> returnValue = underTest.getTraces(servicesAndAllTraces.services);

        // Then
        assertThat(returnValue).hasSize(6);
        returnValue.forEach(trace -> assertThat(trace).hasSize(2));
        assertThat(returnValue).containsExactly(
                servicesAndAllTraces.allTraces.get(0),
                servicesAndAllTraces.allTraces.get(1),
                // Traces for service 1 span 2 are not included
                servicesAndAllTraces.allTraces.get(4),
                servicesAndAllTraces.allTraces.get(5),
                servicesAndAllTraces.allTraces.get(6),
                servicesAndAllTraces.allTraces.get(7));
        List<SimplifiedLogEvent> events = logCaptor.getSimplifiedEvents();
        assertThat(events).containsExactly(
            new SimplifiedLogEvent(Level.INFO, "Retrieving traces for service \"test-service-1\" and span \"test-service-1-span-1\""),
            new SimplifiedLogEvent(Level.INFO, "Retrieved 2 traces for service \"test-service-1\" and span \"test-service-1-span-1\""),
            new SimplifiedLogEvent(Level.INFO, "Retrieving traces for service \"test-service-1\" and span \"test-service-1-span-2\""),
            // Check error was logged
            new SimplifiedLogEvent(Level.ERROR, "Failed to retrieve traces for service \"test-service-1\" and span name \"test-service-1-span-2\""),
            // Check count was zero
            new SimplifiedLogEvent(Level.INFO, "Retrieved 0 traces for service \"test-service-1\" and span \"test-service-1-span-2\""),
            new SimplifiedLogEvent(Level.INFO, "Retrieving traces for service \"test-service-2\" and span \"test-service-2-span-1\""),
            new SimplifiedLogEvent(Level.INFO, "Retrieved 2 traces for service \"test-service-2\" and span \"test-service-2-span-1\""),
            new SimplifiedLogEvent(Level.INFO, "Retrieving traces for service \"test-service-2\" and span \"test-service-2-span-2\""),
            new SimplifiedLogEvent(Level.INFO, "Retrieved 2 traces for service \"test-service-2\" and span \"test-service-2-span-2\""));
    }

    private ServicesAndAllTraces createServicesAndAllTraces() {
        List<Service> services = new ArrayList<>();
        List<List<Span>> allTraces = new ArrayList<>();

        IntStream.range(1, 3).forEach(serviceNumber -> {
            String serviceName = "test-service-" + serviceNumber;
            List<String> spanNames = new ArrayList<>();

            IntStream.range(1, 3).forEach(spanNumber -> {
                String spanName = serviceName + "-span-" + spanNumber;
                List<List<Span>> traces = new ArrayList<>();

                IntStream.range(1, 3).forEach(traceNumber -> {
                    String traceId = "test-service-1-span-1-trace-1";
                    List<Span> trace = new ArrayList<>();

                    IntStream.range(1, 3).forEach(traceSpanNumber -> trace.add(Span.builder()
                            .traceId(traceId)
                            .id(traceId + "-span-" + traceSpanNumber)
                            .build()));

                    traces.add(trace);
                });

                when(client.getTraces(serviceName, spanName, TEST_TRACE_LIMIT)).thenReturn(traces);
                spanNames.add(spanName);
                allTraces.addAll(traces);
            });

            services.add(new Service(serviceName, spanNames));
        });

        return new ServicesAndAllTraces(services, allTraces);
    }

    @RequiredArgsConstructor
    private static class ServicesAndAllTraces {

        private final List<Service> services;
        private final List<List<Span>> allTraces;
    }
}
