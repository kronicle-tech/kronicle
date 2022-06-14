package tech.kronicle.tracingprocessor.internal.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@Builder
@With
public class CollatorGraphEdge {

    Integer sourceIndex;
    Integer targetIndex;
    List<Integer> relatedIndexes;
    String type;
    String label;
    String description;
    Integer sampleSize;
    List<Long> timestamps;
    List<Long> durations;

    public CollatorGraphEdge(
            Integer sourceIndex,
            Integer targetIndex,
            List<Integer> relatedIndexes,
            String type,
            String label,
            String description,
            Integer sampleSize,
            List<Long> timestamps,
            List<Long> durations
    ) {
        this.sourceIndex = sourceIndex;
        this.targetIndex = targetIndex;
        this.relatedIndexes = createUnmodifiableList(relatedIndexes);
        this.type = type;
        this.label = label;
        this.description = description;
        this.sampleSize = sampleSize;
        this.timestamps = createUnmodifiableList(timestamps);
        this.durations = createUnmodifiableList(durations);
    }
}
