package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import tech.kronicle.sdk.constants.PatternStrings;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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
