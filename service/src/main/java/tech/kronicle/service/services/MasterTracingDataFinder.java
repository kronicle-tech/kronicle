package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.finders.TracingDataFinder;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.ComponentMetadata;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterTracingDataFinder {

    private final FinderExtensionRegistry registry;
    private final ExtensionExecutor executor;

    public List<TracingData> findTracingData(ComponentMetadata componentMetadata) {
        return registry.getTracingDataFinders().stream()
                .map(finder -> executeFinder(finder, componentMetadata))
                .distinct()
                .collect(Collectors.toList());
    }

    private TracingData executeFinder(TracingDataFinder finder, ComponentMetadata componentMetadata) {
        Output<TracingData, Void> tracingData = executor.executeFinder(finder, null, componentMetadata);
        if (tracingData.success()) {
            log.info("Tracing data finder {} found {} dependencies", finder.id(), tracingData.getOutput().getDependencies().size());
            log.info("Tracing data finder {} found {} traces", finder.id(), tracingData.getOutput().getTraces().size());
        }
        return tracingData.getOutputOrElse(TracingData.EMPTY);
    }
}
