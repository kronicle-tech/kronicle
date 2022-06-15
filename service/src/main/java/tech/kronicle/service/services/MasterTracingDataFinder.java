package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.finders.TracingDataFinder;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.ComponentMetadata;

import java.util.Collection;
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
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<TracingData> executeFinder(TracingDataFinder finder, ComponentMetadata componentMetadata) {
        Output<List<TracingData>, Void> output = executor.executeFinder(finder, null, componentMetadata);
        if (output.success()) {
            List<TracingData> tracingDatas = output.getOutput();
            log.info("Tracing data finder {} found {} trace datas", finder.id(), tracingDatas.size());
            log.info("Tracing data finder {} found {} dependencies", finder.id(), getDependencyCount(tracingDatas));
            log.info("Tracing data finder {} found {} traces", finder.id(), getTraceCount(tracingDatas));
        }
        return output.getOutputOrElse(List.of());
    }

    private int getDependencyCount(List<TracingData> tracingDatas) {
        return tracingDatas.stream()
                .map(TracingData::getDependencies)
                .mapToInt(Collection::size)
                .sum();
    }

    private int getTraceCount(List<TracingData> tracingDatas) {
        return tracingDatas.stream()
                .map(TracingData::getTraces)
                .mapToInt(Collection::size)
                .sum();
    }
}
