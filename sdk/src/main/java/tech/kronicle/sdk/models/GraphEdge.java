package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class GraphEdge {

    @Min(0)
    Integer sourceIndex;
    @NotNull
    @Min(0)
    Integer targetIndex;
    @NotNull
    List<@NotNull @Min(0) Integer> relatedIndexes;
    @NotBlank
    String type;
    String label;
    String description;
    Integer sampleSize;
    LocalDateTime startTimestamp;
    LocalDateTime endTimestamp;
    GraphEdgeDuration duration;

    public GraphEdge(
            Integer sourceIndex,
            Integer targetIndex,
            List<Integer> relatedIndexes,
            String type,
            String label,
            String description,
            Integer sampleSize,
            LocalDateTime startTimestamp,
            LocalDateTime endTimestamp,
            GraphEdgeDuration duration
    ) {
        this.sourceIndex = sourceIndex;
        this.targetIndex = targetIndex;
        this.relatedIndexes = createUnmodifiableList(relatedIndexes);
        this.type = type;
        this.label = label;
        this.description = description;
        this.sampleSize = sampleSize;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.duration = duration;
    }
}
