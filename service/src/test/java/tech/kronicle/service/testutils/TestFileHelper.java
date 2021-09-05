package tech.kronicle.service.testutils;

import tech.kronicle.service.scanners.gradle.internal.constants.GradleFileNames;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.isNull;

public class TestFileHelper {

    public static String readTestFile(String name, Class<?> type) {
        return readTestFile(getResourcesDir(name, type));
    }

    private static String readTestFile(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getResourcesDir(String name, Class<?> type) {
        return getResourcesDir(type).resolve(name);
    }

    private static Path getResourcesDir(Class<?> type) {
        return getRootDir()
                .resolve("service")
                .resolve("src/test/resources")
                .resolve(String.join("/", type.getName().split("\\.")));
    }

    public static Path getRootDir() {
        Path dir = Path.of("").toAbsolutePath();

        while (!Files.exists(dir.resolve(GradleFileNames.SETTINGS_GRADLE))) {
            dir = dir.getParent();

            if (isNull(dir)) {
                throw new RuntimeException("Cannot find root directory of codebase");
            }
        }

        return dir;
    }
}
