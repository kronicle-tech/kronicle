package tech.kronicle.service.testutils;

import lombok.Value;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.sdk.models.DiagramConnection;

import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.service.testutils.ComponentUtils.createComponentId;

public final class DiagramUtils {

    public static Diagram createDiagram(int diagramNumber) {
        return createDiagram(diagramNumber, diagramNumber, List.of());
    }

    public static Diagram createDiagram(int diagramIdNumber, int diagramOthersNumber) {
        return createDiagram(diagramIdNumber, diagramOthersNumber, List.of());
    }

    public static Diagram createDiagram(
            int diagramNumber,
            List<ComponentNumbersForConnection> componentNumbersForConnections
    ) {
        return createDiagram(diagramNumber, diagramNumber, componentNumbersForConnections);
    }

    public static Diagram createDiagram(
            int diagramIdNumber,
            int diagramOthersNumber,
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
                .id("test-diagram-id-" + diagramIdNumber)
                .name("Test Diagram " + diagramOthersNumber)
                .type("test-type-" + diagramOthersNumber)
                .connections(connections)
                .build();
    }

    public static Diagram createInvalidDiagram(int number) {
        return Diagram.builder()
                .id("test-diagram-id-" + number)
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
