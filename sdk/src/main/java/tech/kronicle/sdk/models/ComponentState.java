package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class ComponentState {

    List<@NotNull @Valid ComponentStateEnvironment> environments;
}
