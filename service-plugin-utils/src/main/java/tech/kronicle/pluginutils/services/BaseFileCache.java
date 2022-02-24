package tech.kronicle.pluginutils.services;

import tech.kronicle.pluginutils.utils.FileUtils;
import tech.kronicle.common.utils.StringEscapeUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class BaseFileCache {

    private final FileUtils fileUtils;
    private final Path cacheDir;

    public BaseFileCache(FileUtils fileUtils, Path cacheDir) throws IOException {
        this.fileUtils = fileUtils;
        this.cacheDir = cacheDir;
        Files.createDirectories(this.cacheDir);
    }

    protected Optional<String> getFileContent(String key) {
        return readFileContent(getAbsoluteFile(key));
    }

    protected void putFileContent(String key, String content) {
        writeFileContent(getAbsoluteFile(key), content);
    }

    private void writeFileContent(Path file, String content) {
        fileUtils.writeFileContent(file, content, CREATE_NEW);
    }

    private Optional<String> readFileContent(Path file) {
        if (!Files.exists(file)) {
            return Optional.empty();
        }

        return Optional.of(fileUtils.readFileContent(file));
    }

    private Path getAbsoluteFile(String key) {
        Path file = cacheDir.resolve(convertKeyToFileName(key));

        if (!file.startsWith(cacheDir)) {
            throw new RuntimeException(String.format("File \"%s\" is outside of cache dir \"%s\"", StringEscapeUtils.escapeString(file.toString()),
                    StringEscapeUtils.escapeString(cacheDir.toString())));
        }

        return file;
    }

    private String convertKeyToFileName(String key) {
        try {
            return URLEncoder.encode(key, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
