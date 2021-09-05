package tech.kronicle.service.utils;

import lombok.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
public class FileUtils {

    private static final int DEFAULT_MAX_DEPTH = Integer.MAX_VALUE;
    private static final BiPredicate<Path, BasicFileAttributes> ALWAYS_TRUE_MATCHER = (ignored1, ignored2) -> true;
    private static final String GIT_DIR_NAME = ".git";

    public String readFileContent(Path file) {
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream openFile(Path file) {
        try {
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeFileContent(Path file, String content, OpenOption... options) {
        try {
            Files.writeString(file, content, StandardCharsets.UTF_8, options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Properties loadProperties(Path file) {
        Properties properties = new Properties();
        try {
            try (InputStream inputStream = Files.newInputStream(file)) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    public Stream<Path> findFiles(Path start) {
        return findFiles(start, DEFAULT_MAX_DEPTH, ALWAYS_TRUE_MATCHER);
    }

    public Stream<Path> findFiles(Path start, int maxDepth) {
        return findFiles(start, maxDepth, ALWAYS_TRUE_MATCHER);
    }

    public Stream<Path> findFiles(Path start, BiPredicate<Path, BasicFileAttributes> matcher) {
        return findFiles(start, DEFAULT_MAX_DEPTH, matcher);
    }

    public Stream<Path> findFiles(Path start, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher) {
        try {
            return Files.find(start, maxDepth, matchFilesThatAreNotGitFiles(start).and(matcher));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stream<FileContent> findFileContents(Path start) {
        return findFileContents(start, DEFAULT_MAX_DEPTH, ALWAYS_TRUE_MATCHER);
    }

    public Stream<FileContent> findFileContents(Path start, int maxDepth) {
        return findFileContents(start, maxDepth, ALWAYS_TRUE_MATCHER);
    }

    public Stream<FileContent> findFileContents(Path start, BiPredicate<Path, BasicFileAttributes> matcher) {
        return findFileContents(start, DEFAULT_MAX_DEPTH, matcher);
    }

    public Stream<FileContent> findFileContents(Path start, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher) {
        return findFiles(start, maxDepth, matcher)
                .map(file -> new FileContent(file, readFileContent(file)))
                .filter(fileContent -> nonNull(fileContent.getContent()));
    }

    private BiPredicate<Path, BasicFileAttributes> matchFilesThatAreNotGitFiles(Path start) {
        return (file, attributes) -> {
            if (!attributes.isRegularFile()) {
                return false;
            }

            return isNotGitPath(start, file);
        };
    }

    private boolean isNotGitPath(Path start, Path file) {
        if (file.getFileName().toString().equals(GIT_DIR_NAME)) {
            return false;
        }

        if (file.equals(start)) {
            return true;
        }

        return isNotGitPath(start, file.getParent());
    }

    @Value
    public static class FileContent {

        Path file;
        String content;
    }
}
