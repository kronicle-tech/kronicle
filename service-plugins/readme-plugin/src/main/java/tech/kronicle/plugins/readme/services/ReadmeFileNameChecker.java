package tech.kronicle.plugins.readme.services;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.regex.Pattern;

@Service
public class ReadmeFileNameChecker {

    private static final Pattern README_FILE_NAME_PATTERN = Pattern.compile("(?i)^readme\\.[a-z]+$");

    public boolean fileNameIsReadmeFileName(Path path) {
        return README_FILE_NAME_PATTERN.matcher(path.getFileName().toString()).matches();
    }
}
