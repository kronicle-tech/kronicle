package tech.kronicle.service.services.testutils;

import lombok.RequiredArgsConstructor;
import org.pf4j.ExtensionFactory;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginStateListener;
import org.pf4j.PluginWrapper;
import org.pf4j.RuntimeMode;
import org.pf4j.VersionManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
public class FakePluginManager<I> implements PluginManager {

    private final List<I> items;
    private final Class<I> itemType;

    @Override
    public List<PluginWrapper> getPlugins() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<PluginWrapper> getPlugins(PluginState pluginState) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<PluginWrapper> getResolvedPlugins() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<PluginWrapper> getUnresolvedPlugins() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<PluginWrapper> getStartedPlugins() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public PluginWrapper getPlugin(String pluginId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void loadPlugins() {

    }

    @Override
    public String loadPlugin(Path pluginPath) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void startPlugins() {

    }

    @Override
    public PluginState startPlugin(String pluginId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void stopPlugins() {

    }

    @Override
    public PluginState stopPlugin(String pluginId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void unloadPlugins() {

    }

    @Override
    public boolean unloadPlugin(String pluginId) {
        return false;
    }

    @Override
    public boolean disablePlugin(String pluginId) {
        return false;
    }

    @Override
    public boolean enablePlugin(String pluginId) {
        return false;
    }

    @Override
    public boolean deletePlugin(String pluginId) {
        return false;
    }

    @Override
    public ClassLoader getPluginClassLoader(String pluginId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Class<?>> getExtensionClasses(String pluginId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T> List<Class<? extends T>> getExtensionClasses(Class<T> type) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T> List<Class<? extends T>> getExtensionClasses(Class<T> type, String pluginId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T> List<T> getExtensions(Class<T> type) {
        if (!Objects.equals(type, itemType)) {
            throw new RuntimeException("Not implemented");
        }
        return (List<T>) items;
    }

    @Override
    public <T> List<T> getExtensions(Class<T> type, String pluginId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List getExtensions(String pluginId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set<String> getExtensionClassNames(String pluginId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ExtensionFactory getExtensionFactory() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public RuntimeMode getRuntimeMode() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public PluginWrapper whichPlugin(Class<?> clazz) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void addPluginStateListener(PluginStateListener listener) {

    }

    @Override
    public void removePluginStateListener(PluginStateListener listener) {

    }

    @Override
    public void setSystemVersion(String version) {

    }

    @Override
    public String getSystemVersion() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Path getPluginsRoot() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Path> getPluginsRoots() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public VersionManager getVersionManager() {
        throw new RuntimeException("Not implemented");
    }
}
