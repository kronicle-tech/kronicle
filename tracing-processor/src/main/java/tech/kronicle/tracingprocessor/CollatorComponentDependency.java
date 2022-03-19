package tech.kronicle.tracingprocessor;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import tech.kronicle.sdk.models.ObjectWithSourceIndexAndTargetIndex;

import java.util.List;

import static java.util.Objects.nonNull;

@Value
@Builder
@With
public class CollatorComponentDependency implements ObjectWithSourceIndexAndTargetIndex, ObjectWithTimestamps, ObjectWithDurations {

    Integer sourceIndex;
    Integer targetIndex;
    List<Integer> relatedIndexes;
    Long timestamp;
    Long duration;

    @Override
    public List<Long> getTimestamps() {
        return nonNull(timestamp) ? List.of(timestamp) : List.of();
    }

    @Override
    public List<Long> getDurations() {
        return nonNull(duration) ? List.of(duration) : List.of();
    }
}
