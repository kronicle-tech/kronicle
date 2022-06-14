package tech.kronicle.tracingprocessor.internal.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import tech.kronicle.sdk.models.GraphNode;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@Builder
@With
public class CollatorGraph {

    List<GraphNode> nodes;
    List<CollatorGraphEdge> edges;
    Integer sampleSize;

    public CollatorGraph(List<GraphNode> nodes, List<CollatorGraphEdge> edges, Integer sampleSize) {
        this.nodes = createUnmodifiableList(nodes);
        this.edges = createUnmodifiableList(edges);
        this.sampleSize = sampleSize;
    }
}
