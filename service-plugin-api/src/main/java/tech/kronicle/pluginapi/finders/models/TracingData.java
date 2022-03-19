package tech.kronicle.pluginapi.finders.models;

import lombok.Builder;
import lombok.Value;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@Builder
public class TracingData {

    public static final TracingData EMPTY = TracingData.builder().build();

    List<Dependency> dependencies;
    List<GenericTrace> traces;

    public TracingData(List<Dependency> dependencies, List<GenericTrace> traces) {
        this.dependencies = createUnmodifiableList(dependencies);
        this.traces = createUnmodifiableList(traces);
    }
}
