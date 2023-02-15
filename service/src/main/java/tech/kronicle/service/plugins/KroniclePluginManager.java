package tech.kronicle.service.plugins;

import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;
import org.pf4j.PluginFactory;
import org.pf4j.PluginLoader;
import org.pf4j.PluginStatusProvider;
import org.pf4j.RuntimeMode;
import org.springframework.context.ConfigurableApplicationContext;
import tech.kronicle.service.plugins.config.PluginManagerConfig;

import jakarta.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public abstract class KroniclePluginManager extends DefaultPluginManager {

    protected abstract PluginManagerConfig getPluginManagerConfig();

    protected abstract ConfigurableApplicationContext getApplicationContext();

    @Override
    public abstract String getSystemVersion();

    @Override
    public RuntimeMode getRuntimeMode() {
        return RuntimeMode.byName(getPluginManagerConfig().getMode());
    }

    @Override
    public List<Path> getPluginsRoots() {
        return getPluginManagerConfig().getPluginRootDirs().stream()
                .map(Path::of)
                .collect(Collectors.toList());
    }

    @Override
    protected PluginFactory createPluginFactory() {
        return new KroniclePluginFactory(getApplicationContext().getEnvironment());
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new KronicleExtensionFactory(this);
    }

    @Override
    protected PluginStatusProvider createPluginStatusProvider() {
        return new KroniclePluginStatusProvider(getApplicationContext().getEnvironment());
    }

    @Override
    protected PluginLoader createPluginLoader() {
        return new KronicleJarPluginLoader(this);
    }

    @PostConstruct
    public void init() {
        loadPlugins();
        startPlugins();
    }
}
