package tech.kronicle.service.testutils;

public final class DiagramStateUtils {

    public static FakeDiagramState createDiagramState(int diagramStateNumber) {
        return new FakeDiagramState(
                "test-diagram-state-type-" + diagramStateNumber,
                "test-plugin-id-" + diagramStateNumber
        );
    }

    private DiagramStateUtils() {
    }
}
