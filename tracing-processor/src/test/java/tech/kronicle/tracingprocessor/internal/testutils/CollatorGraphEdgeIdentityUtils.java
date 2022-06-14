package tech.kronicle.tracingprocessor.internal.testutils;

import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdgeIdentity;

public final class CollatorGraphEdgeIdentityUtils {

    public static CollatorGraphEdgeIdentity createCollatorEdgeIdentity(int edgeIdentityNumber) {
        return CollatorGraphEdgeIdentity.builder()
                .label("test-edge-label-" + edgeIdentityNumber)
                .description("test-description-label-" + edgeIdentityNumber)
                .build();
    }

    private CollatorGraphEdgeIdentityUtils() {
    }
}
