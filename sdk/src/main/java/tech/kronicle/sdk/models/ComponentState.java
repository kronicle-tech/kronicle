package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class ComponentState {

    public static final ComponentState EMPTY = ComponentState.builder().build();

    List<@NotNull @Valid EnvironmentState> environments;

    public ComponentState(List<@NotNull @Valid EnvironmentState> environments) {
        this.environments = createUnmodifiableList(environments);
    }

    public ComponentState withUpdatedEnvironment(
            String environmentId,
            UnaryOperator<EnvironmentState> action
    ) {
        List<EnvironmentState> newEnvironments = new ArrayList<>(environments);
        OptionalInt environmentIndex = IntStream.range(0, newEnvironments.size())
                .filter(it -> Objects.equals(newEnvironments.get(it).getId(), environmentId))
                .findFirst();

        EnvironmentState environment;
        if (environmentIndex.isPresent()) {
            environment = newEnvironments.get(environmentIndex.getAsInt());
        } else {
            environment = EnvironmentState.builder()
                    .id(environmentId)
                    .build();
        }

        environment = action.apply(environment);

        if (environmentIndex.isPresent()) {
            newEnvironments.set(environmentIndex.getAsInt(), environment);
        } else {
            newEnvironments.add(environment);
        }

        return withEnvironments(newEnvironments);
    }
}
