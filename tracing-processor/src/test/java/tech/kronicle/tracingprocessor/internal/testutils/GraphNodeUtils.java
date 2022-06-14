package tech.kronicle.tracingprocessor.internal.testutils;

import tech.kronicle.sdk.models.GraphNode;

public final class GraphNodeUtils {

    public static GraphNode createNode(int nodeNumber) {
        return GraphNode.builder()
                .componentId("test-component-id-" + nodeNumber)
                .build();
    }

    public static GraphNode createNode(String componentId) {
        return GraphNode.builder()
                .componentId(componentId)
                .build();
    }

    private GraphNodeUtils() {
    }
}
