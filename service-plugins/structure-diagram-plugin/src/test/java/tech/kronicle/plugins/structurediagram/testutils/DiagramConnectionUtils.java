package tech.kronicle.plugins.structurediagram.testutils;

import tech.kronicle.sdk.models.DiagramConnection;

public final class DiagramConnectionUtils {

    public static DiagramConnection createDiagramConnection(int diagramConnectionNumber) {
        return createDiagramConnection(diagramConnectionNumber, null);
    }

    public static DiagramConnection createDiagramConnection(int diagramConnectionNumber, String environmentId) {
        return DiagramConnection.builder()
                .sourceComponentId("test-source-component-id-" + diagramConnectionNumber)
                .targetComponentId("test-target-component-id-" + diagramConnectionNumber)
                .environmentId(environmentId)
                .build();
    }

    private DiagramConnectionUtils() {
    }
}
