package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;
import static tech.kronicle.sdk.utils.ListUtils.unmodifiableUnionOfLists;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class EnvironmentPluginState implements ObjectWithIdAndMerge<EnvironmentPluginState> {

    @NotEmpty
    @Pattern(regexp = PatternStrings.ID)
    String id;
    List<@NotNull @Valid CheckState> checks;
    List<@NotNull @Valid LogSummaryState> logSummaries;

    public EnvironmentPluginState(
            String id,
            List<CheckState> checks,
            List<LogSummaryState> logSummaries
    ) {
        this.id = id;
        this.checks = createUnmodifiableList(checks);
        this.logSummaries = createUnmodifiableList(logSummaries);
    }

    @Override
    public EnvironmentPluginState merge(EnvironmentPluginState state) {
        return withChecks(unmodifiableUnionOfLists(List.of(checks, state.checks)))
                .withLogSummaries(unmodifiableUnionOfLists(List.of(logSummaries, state.logSummaries)));
    }

    public EnvironmentPluginState addCheck(CheckState check) {
        return withChecks(unmodifiableUnionOfLists(List.of(checks, List.of(check))));
    }

    public EnvironmentPluginState addLogSummary(LogSummaryState logSummary) {
        return withLogSummaries(unmodifiableUnionOfLists(List.of(logSummaries, List.of(logSummary))));
    }
}
