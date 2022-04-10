package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
public class EnvironmentState {
    
    @NotEmpty
    @Pattern(regexp = PatternStrings.ID)
    String id;
    List<@NotNull @Valid EnvironmentPluginState> plugins;

    public EnvironmentState(String id, List<EnvironmentPluginState> plugins) {
        this.id = id;
        this.plugins = createUnmodifiableList(plugins);
    }

    public EnvironmentState withUpdatedPlugin(
            String pluginId,
            UnaryOperator<EnvironmentPluginState> action
    ) {
        List<EnvironmentPluginState> newPlugins = new ArrayList<>(plugins);
        OptionalInt pluginIndex = IntStream.range(0, newPlugins.size())
                .filter(it -> Objects.equals(newPlugins.get(it).getId(), pluginId))
                .findFirst();

        EnvironmentPluginState plugin;
        if (pluginIndex.isPresent()) {
            plugin = newPlugins.get(pluginIndex.getAsInt());
        } else {
            plugin = EnvironmentPluginState.builder()
                    .id(pluginId)
                    .build();
        }

        plugin = action.apply(plugin);

        if (pluginIndex.isPresent()) {
            newPlugins.set(pluginIndex.getAsInt(), plugin);
        } else {
            newPlugins.add(plugin);
        }

        return withPlugins(newPlugins);
    }
}
