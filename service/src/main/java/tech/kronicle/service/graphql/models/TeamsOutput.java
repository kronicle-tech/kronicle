package tech.kronicle.service.graphql.models;

import lombok.Value;
import tech.kronicle.sdk.models.Team;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
public class TeamsOutput {

    @NotNull
    List<@NotNull @Valid Team> teams;
}
