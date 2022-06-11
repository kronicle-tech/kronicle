package tech.kronicle.service.plugins;

import lombok.SneakyThrows;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableList;

public class KroniclePluginClassLoader extends PluginClassLoader {

    private static final List<String> PREFIXES_FOR_PARENT = List.of(
            "javax.annotation.",
            "tech.kronicle.sdk.",
            "tech.kronicle.componentmetadata.",
            "tech.kronicle.pluginapi.",
            "org.slf4j."
    );

    public KroniclePluginClassLoader(PluginManager pluginManager, PluginDescriptor pluginDescriptor, ClassLoader parent) {
        super(pluginManager, pluginDescriptor, parent);

        addPluginLibsToClassPath();
    }

    private void addPluginLibsToClassPath() {
        List<Path> pluginLibFiles = getPluginLibFiles();
        pluginLibFiles.stream()
                .map(Path::toFile)
                .forEach(this::addFile);
    }

    private List<Path> getPluginLibFiles() {
        try (Stream<Path> stream = findPluginLibFiles()) {
            return stream.collect(toUnmodifiableList());
        }
    }

    @SneakyThrows
    private Stream<Path> findPluginLibFiles() {
        return Files.find(Path.of("plugin-libs"), 1, this::fileIsJar);
    }

    private boolean fileIsJar(Path path, BasicFileAttributes basicFileAttributes) {
        return basicFileAttributes.isRegularFile() && path.getFileName().toString().endsWith(".jar");
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(className)) {
            if (classNameMatchesPrefixForParent(className)) {
                return getParent().loadClass(className);
            }

            return super.loadClass(className);
        }
    }

    private boolean classNameMatchesPrefixForParent(String className) {
        return PREFIXES_FOR_PARENT.stream().anyMatch(className::startsWith);
    }
}
