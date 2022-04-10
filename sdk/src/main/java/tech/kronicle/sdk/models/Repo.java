package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.function.UnaryOperator;

import static java.util.Objects.nonNull;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class Repo implements ObjectWithReference {

    @NotBlank
    String url;
    @NotNull
    Boolean hasComponentMetadataFile;
    @Valid
    ComponentState state;

    @Override
    public String reference() {
        return url;
    }

    public Repo withUpdatedState(UnaryOperator<ComponentState> action) {
        return withState(
                action.apply(nonNull(state) ? state : ComponentState.builder().build())
        );
    }
}
