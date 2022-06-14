package tech.kronicle.pluginapi.finders.models;

import lombok.Builder;
import lombok.Value;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.models.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@Builder(toBuilder = true)
public class TracingData {

    public static final TracingData EMPTY = TracingData.builder().build();

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String pluginId;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String environmentId;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String id;
    @NotBlank
    String name;
    List<Dependency> dependencies;
    List<GenericTrace> traces;

    public TracingData(
            String pluginId,
            String environmentId,
            String id,
            String name,
            List<Dependency> dependencies,
            List<GenericTrace> traces
    ) {
        this.pluginId = pluginId;
        this.environmentId = environmentId;
        this.id = id;
        this.name = name;
        this.dependencies = createUnmodifiableList(dependencies);
        this.traces = createUnmodifiableList(traces);
    }

    public Diagram toDiagram(List<GraphNode> nodes, List<GraphEdge> edges, Integer sampleSize) {
        return Diagram.builder()
                .id(this.getId())
                .name(this.getName())
                .discovered(true)
                .type("tracing")
                .states(List.of(
                        GraphState.builder()
                                .pluginId(this.getPluginId())
                                .environmentId(this.getEnvironmentId())
                                .nodes(nodes)
                                .edges(edges)
                                .sampleSize(sampleSize)
                                .build()
                ))
                .build();
    }
}
