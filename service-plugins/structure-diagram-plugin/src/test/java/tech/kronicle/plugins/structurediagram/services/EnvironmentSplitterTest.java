package tech.kronicle.plugins.structurediagram.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.plugins.structurediagram.models.EnvironmentIdAndDiagramConnections;
import tech.kronicle.sdk.models.DiagramConnection;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.plugins.structurediagram.testutils.DiagramConnectionUtils.createDiagramConnection;

public class EnvironmentSplitterTest {

    private final EnvironmentSplitter underTest = new EnvironmentSplitter();

    @Test
    public void splitDiagramConnectionsByEnvironmentIdShouldReturnAnEmptyArrayWhenTheDiagramConnectionsListIsEmpty() {
        // Given
        List<DiagramConnection> diagramConnections = List.of();

        // When
        List<EnvironmentIdAndDiagramConnections> returnValue = underTest.splitDiagramConnectionsByEnvironmentId(diagramConnections);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void splitDiagramConnectionsByEnvironmentIdShouldReturnOneItemWhenPassedDiagramConnectionsWithOnlyOneEnvironmentId() {
        // Given
        DiagramConnection diagramConnection1 = createDiagramConnection(1, "test-environment-id-1");
        DiagramConnection diagramConnection2 = createDiagramConnection(2, "test-environment-id-1");
        List<DiagramConnection> diagramConnections = List.of(
                diagramConnection1,
                diagramConnection2
        );

        // When
        List<EnvironmentIdAndDiagramConnections> returnValue = underTest.splitDiagramConnectionsByEnvironmentId(diagramConnections);

        // Then
        assertThat(returnValue).containsExactly(
                new EnvironmentIdAndDiagramConnections("test-environment-id-1", diagramConnections)
        );
    }

    @Test
    public void splitDiagramConnectionsByEnvironmentIdShouldReturnOneItemWhenPassedDiagramConnectionsWithNoEnvironmentId() {
        // Given
        DiagramConnection diagramConnection1 = createDiagramConnection(1, null);
        DiagramConnection diagramConnection2 = createDiagramConnection(2, null);
        List<DiagramConnection> diagramConnections = List.of(
                diagramConnection1,
                diagramConnection2
        );

        // When
        List<EnvironmentIdAndDiagramConnections> returnValue = underTest.splitDiagramConnectionsByEnvironmentId(diagramConnections);

        // Then
        assertThat(returnValue).containsExactly(
                new EnvironmentIdAndDiagramConnections(null, diagramConnections)
        );
    }

    @Test
    public void splitDiagramConnectionsByEnvironmentIdShouldReturnTwoItemsWhenPassedDiagramConnectionsWithTwoEnvironmentIds() {
        // Given
        DiagramConnection diagramConnection1 = createDiagramConnection(1, "test-environment-id-1");
        DiagramConnection diagramConnection2 = createDiagramConnection(2, "test-environment-id-1");
        DiagramConnection diagramConnection3 = createDiagramConnection(3, "test-environment-id-2");
        DiagramConnection diagramConnection4 = createDiagramConnection(4, "test-environment-id-2");
        List<DiagramConnection> diagramConnections = List.of(
                diagramConnection1,
                diagramConnection2,
                diagramConnection3,
                diagramConnection4
        );

        // When
        List<EnvironmentIdAndDiagramConnections> returnValue = underTest.splitDiagramConnectionsByEnvironmentId(diagramConnections);

        // Then
        assertThat(returnValue).containsExactly(
                new EnvironmentIdAndDiagramConnections("test-environment-id-1", List.of(
                        diagramConnection1,
                        diagramConnection2
                )),
                new EnvironmentIdAndDiagramConnections("test-environment-id-2", List.of(
                        diagramConnection3,
                        diagramConnection4
                ))
        );
    }

    @Test
    public void splitDiagramConnectionsByEnvironmentIdShouldReturnThreeItemsWhenPassedDiagramConnectionsWithANullEnvironmentIdAndTwoEnvironmentIds() {
        // Given
        DiagramConnection diagramConnection1 = createDiagramConnection(1, "test-environment-id-1");
        DiagramConnection diagramConnection2 = createDiagramConnection(2, "test-environment-id-1");
        DiagramConnection diagramConnection3 = createDiagramConnection(3, "test-environment-id-2");
        DiagramConnection diagramConnection4 = createDiagramConnection(4, "test-environment-id-2");
        DiagramConnection diagramConnection5 = createDiagramConnection(5, null);
        DiagramConnection diagramConnection6 = createDiagramConnection(6, null);
        List<DiagramConnection> diagramConnections = List.of(
                diagramConnection1,
                diagramConnection2,
                diagramConnection3,
                diagramConnection4,
                diagramConnection5,
                diagramConnection6
        );

        // When
        List<EnvironmentIdAndDiagramConnections> returnValue = underTest.splitDiagramConnectionsByEnvironmentId(diagramConnections);

        // Then
        assertThat(returnValue).containsExactly(
                new EnvironmentIdAndDiagramConnections("test-environment-id-1", List.of(
                        diagramConnection1,
                        diagramConnection2,
                        diagramConnection5,
                        diagramConnection6
                )),
                new EnvironmentIdAndDiagramConnections("test-environment-id-2", List.of(
                        diagramConnection3,
                        diagramConnection4,
                        diagramConnection5,
                        diagramConnection6
                )),
                new EnvironmentIdAndDiagramConnections(null, List.of(
                        diagramConnection5,
                        diagramConnection6
                ))
        );
    }
}
