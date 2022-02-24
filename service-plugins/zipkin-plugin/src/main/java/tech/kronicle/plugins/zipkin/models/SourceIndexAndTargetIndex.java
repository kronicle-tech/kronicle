package tech.kronicle.plugins.zipkin.models;

import tech.kronicle.sdk.models.ObjectWithSourceIndexAndTargetIndex;
import lombok.Value;

@Value
public class SourceIndexAndTargetIndex {

    Integer sourceIndex;
    Integer targetIndex;

    public SourceIndexAndTargetIndex(ObjectWithSourceIndexAndTargetIndex dependency) {
        this.sourceIndex = dependency.getSourceIndex();
        this.targetIndex = dependency.getTargetIndex();
    }
}
