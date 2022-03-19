package tech.kronicle.plugins.zipkin.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.plugins.zipkin.models.api.Span;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class TraceMapper {

    private final SubComponentDependencyTagFilter tagFilter;

    public List<GenericTrace> mapTraces(List<List<Span>> traces) {
        return traces.stream()
                .map(this::mapTrace)
                .collect(Collectors.toList());
    }

    public GenericTrace mapTrace(List<Span> trace) {
        return new GenericTrace(trace.stream()
                .map(this::mapSpan)
                .collect(Collectors.toList()));
    }

    private GenericSpan mapSpan(Span span) {
        return GenericSpan.builder()
                .id(span.getId())
                .parentId(span.getParentId())
                .sourceName(span.getLocalEndpoint().getServiceName())
                .name(span.getName())
                .subComponentTags(tagFilter.filterTags(span))
                .timestamp(span.getTimestamp())
                .duration(span.getDuration())
                .build();
    }
}
