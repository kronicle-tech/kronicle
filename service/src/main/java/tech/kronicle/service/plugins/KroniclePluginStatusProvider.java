package tech.kronicle.service.plugins;

import org.pf4j.PluginStatusProvider;
import org.springframework.core.env.PropertyResolver;

public class KroniclePluginStatusProvider implements PluginStatusProvider {

    private PropertyResolver propertyResolver;

    public KroniclePluginStatusProvider(PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    @Override
    public boolean isPluginDisabled(String pluginId) {
        return !propertyResolver.getProperty("plugins." + pluginId + ".enabled", Boolean.class, false);
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
