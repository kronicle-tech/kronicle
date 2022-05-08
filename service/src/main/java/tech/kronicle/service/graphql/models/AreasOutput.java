package tech.kronicle.service.graphql.models;

import lombok.Value;
import tech.kronicle.sdk.models.Area;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
public class AreasOutput {

    @NotNull
    List<@NotNull @Valid Area> areas;
}
