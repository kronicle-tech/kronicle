package tech.kronicle.plugins.zipkin.models;

import lombok.Value;
import tech.kronicle.sdk.models.ObjectWithSourceIndexAndTargetIndex;

@Value
public class SourceIndexAndTargetIndex {

    Integer sourceIndex;
    Integer targetIndex;

    public SourceIndexAndTargetIndex(ObjectWithSourceIndexAndTargetIndex dependency) {
        this.sourceIndex = dependency.getSourceIndex();
        this.targetIndex = dependency.getTargetIndex();
    }
}
