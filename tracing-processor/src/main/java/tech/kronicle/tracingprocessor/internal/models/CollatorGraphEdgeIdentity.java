package tech.kronicle.tracingprocessor.internal.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
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

    private CollatorGraphEdgeIdentity(
            Integer sourceIndex,
            Integer targetIndex,
            String type,
            String label,
            String description
    ) {
        this.sourceIndex = sourceIndex;
        this.targetIndex = targetIndex;
        this.type = type;
        this.label = label;
        this.description = description;
    }
}
