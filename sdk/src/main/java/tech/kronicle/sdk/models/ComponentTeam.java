package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class ComponentTeam {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String teamId;
    String description;
    ComponentTeamType type;
}
