package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.finders.TracingDataFinder;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.models.ComponentMetadata;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterTracingDataFinder {

    private final FinderExtensionRegistry finderRegistry;

    public List<TracingData> findTracingData(ComponentMetadata componentMetadata) {
        return finderRegistry.getTracingDataFinders().stream()
                .map(finder -> executeFinder(finder, componentMetadata))
                .distinct()
                .collect(Collectors.toList());
    }

    private TracingData executeFinder(TracingDataFinder finder, ComponentMetadata componentMetadata) {
        TracingData tracingData;
        try {
            tracingData = finder.find(componentMetadata);
        } catch (Exception e) {
            log.error("Failed to execute tracing data finder {}", finder.id(), e);
            return TracingData.EMPTY;
        }

        log.info("Tracing data finder {} found {} dependencies", finder.id(), tracingData.getDependencies().size());
        log.info("Tracing data finder {} found {} traces", finder.id(), tracingData.getTraces().size());
        return tracingData;
    }
}
