package tech.kronicle.plugins.readme.services;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class ReadmeFileNameChecker {

    private static final Pattern README_FILE_NAME_PATTERN = Pattern.compile("(?i)^readme\\.[a-z]+$");

    public boolean fileNameIsReadmeFileName(Path path) {
        return README_FILE_NAME_PATTERN.matcher(path.getFileName().toString()).matches();
    }
}
