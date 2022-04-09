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

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class ComponentStateEnvironmentPlugin {

    @NotEmpty
    @Pattern(regexp = PatternStrings.ID)
    String id;
    List<@NotNull @Valid ComponentStateLogSummary> logSummaries;

    public ComponentStateEnvironmentPlugin(
            String id,
            List<@NotNull @Valid ComponentStateLogSummary> logSummaries
    ) {
        this.id = id;
        this.logSummaries = createUnmodifiableList(logSummaries);
    }
}
