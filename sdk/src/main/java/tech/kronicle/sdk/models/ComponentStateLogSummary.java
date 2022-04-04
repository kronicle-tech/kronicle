package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class ComponentStateLogSummary {

    @NotEmpty
    String name;
    @NotNull
    LocalDateTime startTimestamp;
    @NotNull
    LocalDateTime endTimestamp;
    List<@NotNull @Valid ComponentStateLogLevel> levels;
    List<@NotNull @Valid ComponentStateLogSummary> comparisons;
    @NotNull
    LocalDateTime updateTimestamp;

    public ComponentStateLogSummary(
            String name,
            LocalDateTime startTimestamp,
            LocalDateTime endTimestamp,
            List<ComponentStateLogLevel> levels,
            List<ComponentStateLogSummary> comparisons,
            LocalDateTime updateTimestamp
    ) {
        this.name = name;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.levels = createUnmodifiableList(levels);
        this.comparisons = createUnmodifiableList(comparisons);
        this.updateTimestamp = updateTimestamp;
    }
}
