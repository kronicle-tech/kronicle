package tech.kronicle.service.plugins;

import org.pf4j.PluginStatusProvider;

import java.util.List;
import java.util.Set;

public class KroniclePluginStatusProvider implements PluginStatusProvider {

    private final Set<String> enabledPlugins;
    private final Set<String> disabledPlugins;

    public KroniclePluginStatusProvider(List<String> enabledPlugins, List<String> disabledPlugins) {
        this.enabledPlugins = Set.copyOf(enabledPlugins);
        this.disabledPlugins = Set.copyOf(disabledPlugins);
    }

    @Override
    public boolean isPluginDisabled(String pluginId) {
        if (!enabledPlugins.isEmpty()) {
            return !enabledPlugins.contains(pluginId);
        } else {
            return disabledPlugins.contains(pluginId);
        }
    }

    @Override
    public void disablePlugin(String pluginId) {
        throw new IllegalStateException("Disabling a plugin at runtime is not a supported feature");
    }

    @Override
    public void enablePlugin(String pluginId) {
        throw new IllegalStateException("Enabling a plugin at runtime is not a supported feature");
    }
}
