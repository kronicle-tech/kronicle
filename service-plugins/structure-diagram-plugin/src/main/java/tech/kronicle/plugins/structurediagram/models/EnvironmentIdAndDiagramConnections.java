package tech.kronicle.plugins.structurediagram.models;

import lombok.Value;
import tech.kronicle.sdk.models.DiagramConnection;

import java.util.List;

@Value
public class EnvironmentIdAndDiagramConnections {

    String environmentId;
    List<DiagramConnection> diagramConnections;
}
