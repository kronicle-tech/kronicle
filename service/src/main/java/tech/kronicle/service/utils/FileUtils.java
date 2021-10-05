package tech.kronicle.service.utils;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class FileUtils {

    private static final int DEFAULT_MAX_DEPTH = Integer.MAX_VALUE;
    private static final BiPredicate<Path, BasicFileAttributes> ALWAYS_TRUE_MATCHER = (ignored1, ignored2) -> true;
    private static final String GIT_DIR_NAME = ".git";
    private static final String KRONICLEIGNORE_FILE_NAME = ".kronicleignore";
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private final AntStyleIgnoreFileLoader antStyleIgnoreFileLoader;

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
            return Files.find(start, maxDepth, isRegularFile()
                    .and(isNotGitFile(start))
                    .and(isNotToBeIgnored(start))
                    .and(matcher));
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

    public boolean fileExists(Path file) {
        return Files.exists(file);
    }

    public Stream<FileContent> findFileContents(Path start, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher) {
        return findFiles(start, maxDepth, matcher)
                .map(file -> new FileContent(file, readFileContent(file)))
                .filter(fileContent -> nonNull(fileContent.getContent()));
    }

    private BiPredicate<Path, BasicFileAttributes> isRegularFile() {
        return (ignored, attributes) -> attributes.isRegularFile();
    }

    private BiPredicate<Path, BasicFileAttributes> isNotGitFile(Path start) {
        return (file, attributes) -> isNotGitPath(start, file);
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

    private BiPredicate<Path, BasicFileAttributes> isNotToBeIgnored(Path start) {
        List<String> ignorePatterns = getIgnorePatterns(start);
        return (file, attributes) -> {
            String relativeFile = start.relativize(file).toString();
            boolean shouldBeIgnored = ignorePatterns.stream()
                    .anyMatch(pattern -> ANT_PATH_MATCHER.matchStart(pattern, relativeFile));
            return !shouldBeIgnored;
        };
    }

    private List<String> getIgnorePatterns(Path start) {
        return Optional.of(start.resolve(KRONICLEIGNORE_FILE_NAME))
                .filter(Files::exists)
                .map(this::readFileContent)
                .map(antStyleIgnoreFileLoader::load)
                .orElseGet(List::of);
    }

    @Value
    public static class FileContent {

        Path file;
        String content;
    }
}
