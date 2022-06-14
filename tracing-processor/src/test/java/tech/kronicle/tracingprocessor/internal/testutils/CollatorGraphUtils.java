package tech.kronicle.tracingprocessor.internal.testutils;

import tech.kronicle.sdk.models.GraphNode;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraph;

import java.util.List;

public final class CollatorGraphUtils {

    public static CollatorGraph createCollatorGraph(int collatorGraphNumber) {
        return CollatorGraph.builder()
                .nodes((List.of(
                        GraphNode.builder()
                                .componentId("test-component-id-" + collatorGraphNumber)
                                .build()
                )))
                .build();
    }

    private CollatorGraphUtils() {
    }
}
