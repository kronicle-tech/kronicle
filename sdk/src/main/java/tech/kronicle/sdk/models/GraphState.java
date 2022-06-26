package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class GraphState implements DiagramState {

    public static final String TYPE = "graph";

    String type = TYPE;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String pluginId;
    String id = null;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String environmentId;
    List<@Valid GraphNode> nodes;
    List<@Valid GraphEdge> edges;
    Integer sampleSize;

    public GraphState(
            String pluginId,
            String environmentId,
            List<GraphNode> nodes,
            List<GraphEdge> edges,
            Integer sampleSize
    ) {
        this.pluginId = pluginId;
        this.environmentId = environmentId;
        this.nodes = createUnmodifiableList(nodes);
        this.edges = createUnmodifiableList(edges);
        this.sampleSize = sampleSize;
    }
}
