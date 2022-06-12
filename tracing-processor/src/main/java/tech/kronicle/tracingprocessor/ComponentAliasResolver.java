package tech.kronicle.tracingprocessor;

import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ComponentAliasResolver {

    public List<TracingData> tracingDataList(List<TracingData> tracingDataList, Map<String, String> componentAliasMap) {
        return tracingDataList.stream()
                .map(tracingData -> tracingData(tracingData, componentAliasMap))
                .collect(Collectors.toList());
    }

    private TracingData tracingData(TracingData tracingData, Map<String, String> componentAliasMap) {
        return tracingData.toBuilder()
                .dependencies(dependencies(tracingData.getDependencies(), componentAliasMap))
                .traces(traces(tracingData.getTraces(), componentAliasMap))
                .build();
    }

    private List<Dependency> dependencies(List<Dependency> dependencies, Map<String, String> componentAliasMap) {
        return dependencies.stream()
                .map(dependency -> dependency(dependency, componentAliasMap))
                .collect(Collectors.toList());
    }

    private Dependency dependency(Dependency dependency, Map<String, String> componentAliasMap) {
        return dependency.toBuilder()
                .sourceComponentId(componentId(dependency.getSourceComponentId(), componentAliasMap))
                .targetComponentId(componentId(dependency.getTargetComponentId(), componentAliasMap))
                .build();
    }

    private List<GenericTrace> traces(List<GenericTrace> traces, Map<String, String> componentAliasMap) {
        return traces.stream()
                .map(trace -> trace(trace, componentAliasMap))
                .collect(Collectors.toList());
    }

    private GenericTrace trace(GenericTrace trace, Map<String, String> componentAliasMap) {
        return new GenericTrace(
                spans(trace.getSpans(), componentAliasMap)
        );
    }

    private List<GenericSpan> spans(List<GenericSpan> spans, Map<String, String> componentAliasMap) {
        return spans.stream()
                .map(span -> span(span, componentAliasMap))
                .collect(Collectors.toList());
    }

    private GenericSpan span(GenericSpan span, Map<String, String> componentAliasMap) {
        return span.withSourceName(componentId(span.getSourceName(), componentAliasMap));
    }

    private String componentId(String componentId, Map<String, String> componentAliasMap) {
        return Optional.ofNullable(componentAliasMap.get(componentId)).orElse(componentId);
    }
}
