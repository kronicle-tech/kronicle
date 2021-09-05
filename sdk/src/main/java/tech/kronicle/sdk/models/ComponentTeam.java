package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class ComponentTeam {

    @NotBlank
    @Pattern(regexp = "[a-z][a-z0-9]*(-[a-z0-9]+)*")
    String teamId;
    String description;
    ComponentTeamType type;
}
