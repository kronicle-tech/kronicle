package tech.kronicle.tracingprocessor.internal.testutils;

import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdge;

import java.util.List;

public final class CollatorGraphEdgeUtils {

    public static CollatorGraphEdge createCollatorEdge(int edgeNumber) {
        return createCollatorEdge(edgeNumber, edgeNumber);
    }

    public static CollatorGraphEdge createCollatorEdge(int edgeIdentityNumber, int edgeOtherNumber) {
        return CollatorGraphEdge.builder()
                .label("test-edge-label-" + edgeIdentityNumber)
                .description("test-description-label-" + edgeIdentityNumber)
                .timestamps(List.of((long) edgeOtherNumber))
                .build();
    }

    private CollatorGraphEdgeUtils() {
    }
}
