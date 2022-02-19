package tech.kronicle.service.testutils;

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
        return getProjectDir()
                .resolve("src/test/resources")
                .resolve(String.join("/", type.getName().split("\\.")));
    }

    public static Path getProjectDir() {
        Path dir = Path.of("").toAbsolutePath();

        while (!Files.exists(dir.resolve("build.gradle"))) {
            dir = dir.getParent();

            if (isNull(dir)) {
                throw new RuntimeException("Cannot find project directory");
            }
        }

        return dir;
    }
}
