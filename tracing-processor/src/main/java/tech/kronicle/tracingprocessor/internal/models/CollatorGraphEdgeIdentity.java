package tech.kronicle.tracingprocessor.internal.models;

import lombok.Value;

@Value
public class CollatorGraphEdgeIdentity {

    Integer sourceIndex;
    Integer targetIndex;
    String type;
    String label;
    String description;

    public CollatorGraphEdgeIdentity(CollatorGraphEdge edge) {
        this.sourceIndex = edge.getSourceIndex();
        this.targetIndex = edge.getTargetIndex();
        this.type = edge.getType();
        this.label = edge.getLabel();
        this.description = edge.getDescription();
    }
}
