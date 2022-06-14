package tech.kronicle.tracingprocessor.internal.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.sdk.models.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DiagramGraphCollator {

    private final NodeHelper nodeHelper;

    public Diagram collateGraph(Diagram diagram) {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphEdge> edges = createEdges(nodes, diagram.getConnections());
        GraphState graph = GraphState.builder()
                .nodes(nodes)
                .edges(edges)
                .build();
        return diagram.addState(graph);
    }

    private List<GraphEdge> createEdges(
            List<GraphNode> nodes,
            List<DiagramConnection> connections
    ) {
        return connections.stream()
                .map(connection -> createEdge(nodes, connection))
                .collect(toUnmodifiableList());
    }

    private GraphEdge createEdge(List<GraphNode> nodes, DiagramConnection connection) {
        return new GraphEdge(
                nodeHelper.getOrAddNode(nodes, connection.getSourceComponentId()),
                nodeHelper.getOrAddNode(nodes, connection.getTargetComponentId()),
                List.of(),
                connection.getType(),
                connection.getLabel(),
                connection.getDescription(),
                null,
                null,
                null,
                null
        );
    }
}
