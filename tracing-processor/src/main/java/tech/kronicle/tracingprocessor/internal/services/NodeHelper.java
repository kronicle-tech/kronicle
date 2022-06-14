package tech.kronicle.tracingprocessor.internal.services;

import tech.kronicle.sdk.models.GraphNode;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class NodeHelper {

    public int getOrAddNode(List<GraphNode> nodes, String componentId) {
        OptionalInt nodeIndexMatch = IntStream.range(0, nodes.size())
                .filter(nodeIndex -> Objects.equals(nodes.get(nodeIndex).getComponentId(), componentId))
                .findFirst();

        if (nodeIndexMatch.isPresent()) {
            return nodeIndexMatch.getAsInt();
        }

        nodes.add(GraphNode.builder().componentId(componentId).build());
        return nodes.size() - 1;
    }
}
