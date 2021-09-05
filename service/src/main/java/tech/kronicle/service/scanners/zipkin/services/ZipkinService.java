package tech.kronicle.service.scanners.zipkin.services;

import tech.kronicle.sdk.models.zipkin.ZipkinDependency;
import tech.kronicle.service.scanners.zipkin.client.ZipkinClient;
import tech.kronicle.service.scanners.zipkin.client.ZipkinClientException;
import tech.kronicle.service.scanners.zipkin.config.ZipkinConfig;
import tech.kronicle.service.scanners.zipkin.models.api.Service;
import tech.kronicle.service.scanners.zipkin.models.api.Span;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Slf4j
@RequiredArgsConstructor
public class ZipkinService {

    private static final int MAX_SPAN_NAMES_PER_SERVICE = 200;

    private final ZipkinClient client;
    private final ZipkinConfig config;

    public List<ZipkinDependency> getDependencies() {
        return client.getDependencies();
    }

    public List<Service> getServices() {
        return client.getServiceNames().stream()
                .map(serviceName -> new Service(serviceName, getSpanNames(serviceName)))
                .collect(Collectors.toList());
    }

    public List<List<Span>> getTraces(List<Service> services) {
        return services
                .stream()
                .flatMap(service -> service.getSpanNames().stream().map(spanName -> new ServiceNameAndSpanName(service.getName(), spanName)))
                .flatMap(serviceNameAndSpanName -> {
                    String serviceName = serviceNameAndSpanName.serviceName;
                    String spanName = serviceNameAndSpanName.spanName;
                    log.info("Retrieving traces for service \"{}\" and span \"{}\"", serviceName, spanName);
                    List<List<Span>> traces = getTraces(serviceName, spanName);
                    log.info("Retrieved {} traces for service \"{}\" and span \"{}\"", traces.size(), serviceName, spanName);
                    return traces.stream();
                })
                .collect(Collectors.toList());
    }

    private List<String> getSpanNames(String serviceName) {
        try {
            List<String> spanNames = client.getSpanNames(serviceName);

            if (spanNames.size() > MAX_SPAN_NAMES_PER_SERVICE) {
                log.warn(String.format("Component %s has %d span names which is greater than the maximum of %d. Only the first %d span names will be used",
                        serviceName, spanNames.size(), MAX_SPAN_NAMES_PER_SERVICE, MAX_SPAN_NAMES_PER_SERVICE));
                return spanNames.subList(0, MAX_SPAN_NAMES_PER_SERVICE);
            }

            return spanNames;
        } catch (ZipkinClientException e) {
            log.error("Failed to retrieve span names for service \"{}\"", serviceName, e);
            return List.of();
        }
    }

    private List<List<Span>> getTraces(String serviceName, String spanName) {
        try {
            return client.getTraces(serviceName, spanName, config.getTraceLimit());
        } catch (ZipkinClientException e) {
            log.error("Failed to retrieve traces for service \"{}\" and span name \"{}\"", serviceName, spanName, e);
            return List.of();
        }
    }

    @RequiredArgsConstructor
    private static class ServiceNameAndSpanName {

        private final String serviceName;
        private final String spanName;
    }
}
