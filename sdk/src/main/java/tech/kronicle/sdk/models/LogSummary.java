package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class LogSummary {

    @NotEmpty
    String name;
    @NotNull
    LocalDateTime startTimestamp;
    @NotNull
    LocalDateTime endTimestamp;
    List<@NotNull @Valid LogLevelSummary> levels;

    public LogSummary(
            String name,
            LocalDateTime startTimestamp,
            LocalDateTime endTimestamp,
            List<LogLevelSummary> levels
    ) {
        this.name = name;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.levels = createUnmodifiableList(levels);
    }
}
