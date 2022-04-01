package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class ComponentStateLogLevelCount {

    @Pattern(regexp = PatternStrings.CASE_INSENSITIVE_SNAKE_CASE_OR_KEBAB_CASE)
    String level;
    @NotNull
    @Min(0)
    Long count;
}
