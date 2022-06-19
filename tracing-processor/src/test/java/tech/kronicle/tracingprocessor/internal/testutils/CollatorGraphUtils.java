package tech.kronicle.tracingprocessor.internal.testutils;

import tech.kronicle.sdk.models.GraphNode;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraph;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdge;

import java.util.List;

public final class CollatorGraphUtils {

    public static CollatorGraph createCollatorGraph(int collatorGraphNumber) {
        return CollatorGraph.builder()
                .nodes(List.of(
                        GraphNode.builder()
                                .componentId("test-component-id-" + collatorGraphNumber + "-1")
                                .build(),
                        GraphNode.builder()
                                .componentId("test-component-id-" + collatorGraphNumber + "-2")
                                .build()
                ))
                .edges(List.of(
                        CollatorGraphEdge.builder()
                                .sourceIndex(0)
                                .targetIndex(1)
                                .timestamps(List.of(1L, 2L))
                                .build()
                ))
                .build();
    }

    private CollatorGraphUtils() {
    }
}
