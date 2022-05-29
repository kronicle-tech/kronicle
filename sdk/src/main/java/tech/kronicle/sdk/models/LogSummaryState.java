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
public class LogSummaryState implements ComponentEnvironmentState {

    String type = "log-summary";
    @NotEmpty
    String pluginId;
    @NotEmpty
    String environmentId;
    @NotEmpty
    String name;
    @NotNull
    LocalDateTime startTimestamp;
    @NotNull
    LocalDateTime endTimestamp;
    List<@NotNull @Valid LogLevelSummary> levels;
    List<@NotNull @Valid LogSummary> comparisons;
    @NotNull
    LocalDateTime updateTimestamp;

    public LogSummaryState(
            String pluginId,
            String environmentId,
            String name,
            LocalDateTime startTimestamp,
            LocalDateTime endTimestamp,
            List<LogLevelSummary> levels,
            List<LogSummary> comparisons,
            LocalDateTime updateTimestamp
    ) {
        this.pluginId = pluginId;
        this.environmentId = environmentId;
        this.name = name;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.levels = createUnmodifiableList(levels);
        this.comparisons = createUnmodifiableList(comparisons);
        this.updateTimestamp = updateTimestamp;
    }

    public static LogSummaryState of(
            String pluginId,
            String environmentId,
            LogSummary logSummary,
            List<LogSummary> comparisons,
            LocalDateTime updateTimestamp
    ) {
        return new LogSummaryState(
                pluginId,
                environmentId,
                logSummary.getName(),
                logSummary.getStartTimestamp(),
                logSummary.getEndTimestamp(),
                logSummary.getLevels(),
                comparisons,
                updateTimestamp
        );
    }
}
