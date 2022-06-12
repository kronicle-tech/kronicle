package tech.kronicle.sdk.models.testutils;

import tech.kronicle.sdk.models.DiagramState;

public final class DiagramStateUtils {

    public static DiagramState createDiagramState(int stateNumber) {
        return createDiagramState(stateNumber, "test-type-" + stateNumber);
    }

    public static DiagramState createDiagramState(int stateNumber, String type) {
        return new FakeDiagramState(
                type,
                "test-plugin-id-" + stateNumber
        );
    }

    private DiagramStateUtils() {
    }
}
