package tech.kronicle.plugins.structurediagram.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.structurediagram.constants.ConnectionTypes;
import tech.kronicle.plugins.structurediagram.models.EnvironmentIdAndDiagramConnections;
import tech.kronicle.sdk.models.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableSet;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class StructureDiagramCreator {

    private final EnvironmentSplitter environmentSplitter;

    public List<Diagram> createStructureDiagrams(ComponentMetadata componentMetadata) {
        List<EnvironmentIdAndDiagramConnections> diagramConnectionsByEnvironmentId =
                environmentSplitter.splitDiagramConnectionsByEnvironmentId(getAllRelevantConnections(componentMetadata));

        return diagramConnectionsByEnvironmentId.stream()
                .map(environmentIdAndDiagramConnections -> createStructureDiagrams(
                        environmentIdAndDiagramConnections.getEnvironmentId(),
                        environmentIdAndDiagramConnections.getDiagramConnections()
                ))
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }

    private List<Diagram> createStructureDiagrams(
            String environmentId,
            List<DiagramConnection> diagramConnectionsForEnvironment
    ) {
        List<Diagram> diagrams = new ArrayList<>();
        LinkedList<DiagramConnection> remainingConnections = new LinkedList<>(diagramConnectionsForEnvironment);

        while (!remainingConnections.isEmpty()) {
            DiagramConnection firstConnection = remainingConnections.removeFirst();
            Set<String> diagramComponentIds = new HashSet<>();
            List<DiagramConnection> diagramConnections = new ArrayList<>();
            addConnectionToDiagram(diagramConnections, diagramComponentIds, firstConnection);
            boolean additionalConnectionFound;

            do {
                additionalConnectionFound = false;

                for (ListIterator<DiagramConnection> iterator = remainingConnections.listIterator(); iterator.hasNext(); ) {
                    DiagramConnection remainingConnection = iterator.next();

                    if (connectionIsRelatedToDiagram(diagramComponentIds, remainingConnection)) {
                        additionalConnectionFound = true;
                        iterator.remove();
                        addConnectionToDiagram(diagramConnections, diagramComponentIds, remainingConnection);
                    }
                }
            } while (additionalConnectionFound);


            Diagram diagram = createDiagram(environmentId, diagramConnections);
            diagrams.add(diagram);
        }

        return diagrams;
    }

    private Diagram createDiagram(String environmentId, List<DiagramConnection> diagramConnections) {
        List<String> ancestorComponentIds = getAncestorComponentIds(diagramConnections);
        return Diagram.builder()
                .id("structure-"
                        + (nonNull(environmentId) ? environmentId + "-" : "")
                        + String.join("-", ancestorComponentIds))
                .name("Structure - "
                        + (nonNull(environmentId) ? environmentId + " - " : "")
                        + String.join(" - ", ancestorComponentIds))
                .discovered(true)
                .description(
                        "An auto-generated diagram that shows the structure of the "
                                + String.join(", ", ancestorComponentIds)
                                + " component" + (ancestorComponentIds.size() != 1 ? "s" : "")
                                + (nonNull(environmentId) ? " in the " + environmentId + " environment" : "")
                )
                .connections(diagramConnections)
                .build();
    }

    private List<DiagramConnection> getAllRelevantConnections(ComponentMetadata componentMetadata) {
        return componentMetadata.getComponents().stream()
                .flatMap(component -> component.getConnections().stream()
                        .map(connection -> mapConnection(component, connection))
                )
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private DiagramConnection mapConnection(Component component, ComponentConnection connection) {
        if (Objects.equals(connection.getType(), ConnectionTypes.SUPER_COMPONENT)) {
            return mapSubComponentConnection(connection.getTargetComponentId(), component.getId(), connection);
        } else if (Objects.equals(connection.getType(), ConnectionTypes.SUB_COMPONENT)) {
            return mapSubComponentConnection(component.getId(), connection.getTargetComponentId(), connection);
        } else {
            return null;
        }
    }

    private DiagramConnection mapSubComponentConnection(String sourceComponentId, String targetComponentId, ComponentConnection connection) {
        return DiagramConnection.builder()
                .sourceComponentId(sourceComponentId)
                .targetComponentId(targetComponentId)
                .type(ConnectionTypes.SUB_COMPONENT)
                .environmentId(connection.getEnvironmentId())
                .label(connection.getLabel())
                .description(connection.getDescription())
                .tags(connection.getTags())
                .build();
    }

    private void addConnectionToDiagram(
            List<DiagramConnection> diagramConnections,
            Set<String> relatedComponentIds,
            DiagramConnection connection
    ) {
        addConnectionComponentIds(relatedComponentIds, connection);
        diagramConnections.add(connection);
    }

    private void addConnectionComponentIds(Set<String> relatedComponentIds, DiagramConnection connection) {
        relatedComponentIds.add(connection.getSourceComponentId());
        relatedComponentIds.add(connection.getTargetComponentId());
    }

    private boolean connectionIsRelatedToDiagram(Set<String> diagramComponentIds, DiagramConnection connection) {
        return diagramComponentIds.contains(connection.getSourceComponentId())
                || diagramComponentIds.contains(connection.getTargetComponentId());
    }

    private List<String> getAncestorComponentIds(List<DiagramConnection> diagramConnections) {
        Set<String> superComponentIds = getSuperComponentIds(diagramConnections);
        Set<String> subComponentIds = getSubComponentIds(diagramConnections);
        Set<String> ancestorComponentIds = new HashSet<>(superComponentIds);
        ancestorComponentIds.removeAll(subComponentIds);
        return ancestorComponentIds.stream()
                .sorted()
                .collect(toUnmodifiableList());
    }

    private Set<String> getSuperComponentIds(List<DiagramConnection> diagramConnections) {
        return diagramConnections.stream()
                .map(DiagramConnection::getSourceComponentId)
                .collect(toUnmodifiableSet());
    }

    private Set<String> getSubComponentIds(List<DiagramConnection> diagramConnections) {
        return diagramConnections.stream()
                .map(DiagramConnection::getTargetComponentId)
                .collect(toUnmodifiableSet());
    }
}
