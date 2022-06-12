package tech.kronicle.service.testutils;

import lombok.Value;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.sdk.models.DiagramConnection;

import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.service.testutils.ComponentUtils.createComponentId;

public final class DiagramUtils {

    public static Diagram createDiagram(int diagramId) {
        return createDiagram(diagramId, List.of());
    }

    public static Diagram createDiagram(
            int diagramId,
            List<ComponentNumbersForConnection> componentNumbersForConnections
    ) {
        List<DiagramConnection> connections = componentNumbersForConnections.stream()
                .map(
                        componentNumbersForConnection -> DiagramConnection.builder()
                                .sourceComponentId(createComponentId(componentNumbersForConnection.sourceComponentNumber))
                                .targetComponentId(createComponentId(componentNumbersForConnection.targetComponentNumber))
                                .build()
                )
                .collect(toUnmodifiableList());
        return Diagram.builder()
                .id("test-diagram-id-" + diagramId)
                .name("Test Diagram " + diagramId)
                .connections(connections)
                .build();
    }

    private DiagramUtils() {
    }

    @Value
    public static class ComponentNumbersForConnection {

        int sourceComponentNumber;
        int targetComponentNumber;
    }
}
