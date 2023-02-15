package tech.kronicle.service.graphql.models;

import lombok.Value;
import tech.kronicle.sdk.models.Component;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Value
public class ComponentsOutput {

    @NotNull
    List<@NotNull @Valid Component> components;
}
