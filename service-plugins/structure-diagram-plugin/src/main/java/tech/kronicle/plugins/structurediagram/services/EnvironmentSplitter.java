package tech.kronicle.plugins.structurediagram.services;

import tech.kronicle.plugins.structurediagram.models.EnvironmentIdAndDiagramConnections;
import tech.kronicle.sdk.models.DiagramConnection;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

public class EnvironmentSplitter {
    public List<EnvironmentIdAndDiagramConnections> splitDiagramConnectionsByEnvironmentId(
            List<DiagramConnection> diagramConnections
    ) {
        return getEnvironmentIdsFromDiagramConnections(diagramConnections).stream()
                .map(environmentId -> createEnvironmentIdAndDiagramConnections(diagramConnections, environmentId))
                .distinct()
                .collect(toUnmodifiableList());
    }

    private List<String> getEnvironmentIdsFromDiagramConnections(List<DiagramConnection> diagramConnections) {
        return diagramConnections.stream()
                .map(DiagramConnection::getEnvironmentId)
                .collect(toList());
    }

    private EnvironmentIdAndDiagramConnections createEnvironmentIdAndDiagramConnections(
            List<DiagramConnection> diagramConnections,
            String environmentId
    ) {
        if (isNull(environmentId)) {
            return new EnvironmentIdAndDiagramConnections(null, diagramConnections);
        } else {
            return new EnvironmentIdAndDiagramConnections(
                    environmentId,
                    getDiagramConnectionsForEnvironmentId(diagramConnections, environmentId)
            );
        }
    }

    private List<DiagramConnection> getDiagramConnectionsForEnvironmentId(List<DiagramConnection> diagramConnections, String environmentId) {
        return diagramConnections.stream()
                .filter(diagramConnection -> Objects.equals(diagramConnection.getEnvironmentId(), environmentId))
                .collect(toUnmodifiableList());
    }
}
