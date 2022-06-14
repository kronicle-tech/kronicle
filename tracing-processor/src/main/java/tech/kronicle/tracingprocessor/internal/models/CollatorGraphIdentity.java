package tech.kronicle.tracingprocessor.internal.models;

import lombok.Value;
import tech.kronicle.sdk.models.GraphNode;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class CollatorGraphIdentity {

    List<GraphNode> nodes;
    List<CollatorGraphEdgeIdentity> edgeIdentities;

    public static CollatorGraphIdentity fromCollatorGraph(CollatorGraph graph) {
        return new CollatorGraphIdentity(
                graph.getNodes(),
                graph.getEdges().stream()
                        .map(CollatorGraphEdgeIdentity::new)
                        .distinct()
                        .collect(Collectors.toList())
        );
    }
}
