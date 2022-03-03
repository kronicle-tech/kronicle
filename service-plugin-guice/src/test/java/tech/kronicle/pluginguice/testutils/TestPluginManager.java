package tech.kronicle.pluginguice.testutils;

import org.pf4j.ExtensionFactory;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginStateListener;
import org.pf4j.PluginWrapper;
import org.pf4j.RuntimeMode;
import org.pf4j.VersionManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class TestPluginManager implements PluginManager {

    @Override
    public List<PluginWrapper> getPlugins() {
        return null;
    }

    @Override
    public List<PluginWrapper> getPlugins(PluginState pluginState) {
        return null;
    }

    @Override
    public List<PluginWrapper> getResolvedPlugins() {
        return null;
    }

    @Override
    public List<PluginWrapper> getUnresolvedPlugins() {
        return null;
    }

    @Override
    public List<PluginWrapper> getStartedPlugins() {
        return null;
    }

    @Override
    public PluginWrapper getPlugin(String pluginId) {
        return null;
    }

    @Override
    public void loadPlugins() {

    }

    @Override
    public String loadPlugin(Path pluginPath) {
        return null;
    }

    @Override
    public void startPlugins() {

    }

    @Override
    public PluginState startPlugin(String pluginId) {
        return null;
    }

    @Override
    public void stopPlugins() {

    }

    @Override
    public PluginState stopPlugin(String pluginId) {
        return null;
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
        return null;
    }

    @Override
    public List<Class<?>> getExtensionClasses(String pluginId) {
        return null;
    }

    @Override
    public <T> List<Class<? extends T>> getExtensionClasses(Class<T> type) {
        return null;
    }

    @Override
    public <T> List<Class<? extends T>> getExtensionClasses(Class<T> type, String pluginId) {
        return null;
    }

    @Override
    public <T> List<T> getExtensions(Class<T> type) {
        return null;
    }

    @Override
    public <T> List<T> getExtensions(Class<T> type, String pluginId) {
        return null;
    }

    @Override
    public List getExtensions(String pluginId) {
        return null;
    }

    @Override
    public Set<String> getExtensionClassNames(String pluginId) {
        return null;
    }

    @Override
    public ExtensionFactory getExtensionFactory() {
        return null;
    }

    @Override
    public RuntimeMode getRuntimeMode() {
        return null;
    }

    @Override
    public PluginWrapper whichPlugin(Class<?> clazz) {
        return null;
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
        return null;
    }

    @Override
    public Path getPluginsRoot() {
        return null;
    }

    @Override
    public List<Path> getPluginsRoots() {
        return null;
    }

    @Override
    public VersionManager getVersionManager() {
        return null;
    }
}
