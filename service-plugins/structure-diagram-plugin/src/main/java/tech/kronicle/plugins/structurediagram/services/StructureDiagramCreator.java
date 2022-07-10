package tech.kronicle.plugins.structurediagram.services;

import tech.kronicle.plugins.structurediagram.constants.ConnectionTypes;
import tech.kronicle.sdk.models.*;

import java.util.*;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableSet;

public class StructureDiagramCreator {

    public List<Diagram> createStructureDiagrams(ComponentMetadata componentMetadata) {
        List<Diagram> diagrams = new ArrayList<>();
        LinkedList<DiagramConnection> remainingConnections = new LinkedList<>(getAllRelevantConnections(componentMetadata));

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

            List<String> ancestorComponentIds = getAncestorComponentIds(diagramConnections);
            Diagram diagram = Diagram.builder()
                    .id("structure-" + String.join("-", ancestorComponentIds))
                    .name("Structure - " + String.join(" - ", ancestorComponentIds))
                    .description("A diagram that shows the structure of the " + String.join(", ", ancestorComponentIds) + " component" + (ancestorComponentIds.size() != 1 ? "s" : ""))
                    .connections(diagramConnections)
                    .build();
            diagrams.add(diagram);
        }

        return diagrams;
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
        switch (connection.getType()) {
            case ConnectionTypes.SUPER_COMPONENT:
                return mapSubComponentConnection(connection.getTargetComponentId(), component.getId(), connection);
            case ConnectionTypes.SUB_COMPONENT:
                return mapSubComponentConnection(component.getId(), connection.getTargetComponentId(), connection);
            default:
                return null;
        }
    }

    private DiagramConnection mapSubComponentConnection(String sourceComponentId, String targetComponentId, ComponentConnection connection) {
        return DiagramConnection.builder()
                .sourceComponentId(sourceComponentId)
                .targetComponentId(targetComponentId)
                .type(ConnectionTypes.SUB_COMPONENT)
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
        Set<String> ancestorComponentIds = getSuperComponentIds(diagramConnections);
        Set<String> subComponentIds = getSubComponentIds(diagramConnections);
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
