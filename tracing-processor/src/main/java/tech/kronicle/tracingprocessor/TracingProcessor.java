package tech.kronicle.tracingprocessor;

import lombok.RequiredArgsConstructor;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.models.Dependency;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class TracingProcessor {

    private final ComponentDependencyCollator componentDependencyCollator;
    private final SubComponentDependencyCollator subComponentDependencyCollator;
    private final CallGraphCollator callGraphCollator;

    public ProcessedTracingData process(List<TracingData> tracingData) {
        List<Dependency> dependencies = getAllDependencies(tracingData);
        List<GenericTrace> traces = getAllTraces(tracingData);

        return new ProcessedTracingData(
                componentDependencyCollator.collateDependencies(traces, dependencies),
                subComponentDependencyCollator.collateDependencies(traces),
                callGraphCollator.collateCallGraphs(traces)
        );
    }

    private List<Dependency> getAllDependencies(List<TracingData> tracingData) {
        return tracingData.stream()
                .map(TracingData::getDependencies)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<GenericTrace> getAllTraces(List<TracingData> tracingData) {
        return tracingData.stream()
                .map(TracingData::getTraces)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
