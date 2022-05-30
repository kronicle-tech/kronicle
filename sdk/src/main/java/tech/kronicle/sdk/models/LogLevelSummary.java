package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class LogLevelSummary {

    @Pattern(regexp = PatternStrings.CASE_INSENSITIVE_SNAKE_CASE_OR_KEBAB_CASE)
    String level;
    @NotNull
    @Min(0)
    Long count;
    List<LogMessageSummary> topMessages;

    public LogLevelSummary(String level, Long count, List<LogMessageSummary> topMessages) {
        this.level = level;
        this.count = count;
        this.topMessages = createUnmodifiableList(topMessages);
    }
}
