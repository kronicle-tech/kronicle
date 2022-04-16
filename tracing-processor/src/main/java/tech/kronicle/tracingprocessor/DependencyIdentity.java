package tech.kronicle.tracingprocessor;

import lombok.Value;
import tech.kronicle.sdk.models.DependencyWithIdentity;

@Value
public class DependencyIdentity {

    Integer sourceIndex;
    Integer targetIndex;
    String typeId;
    String label;
    String description;

    public DependencyIdentity(DependencyWithIdentity dependency) {
        this.sourceIndex = dependency.getSourceIndex();
        this.targetIndex = dependency.getTargetIndex();
        this.typeId = dependency.getTypeId();
        this.label = dependency.getLabel();
        this.description = dependency.getDescription();
    }
}
