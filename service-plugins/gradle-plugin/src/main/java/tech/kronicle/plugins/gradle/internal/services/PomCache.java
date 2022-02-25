package tech.kronicle.plugins.gradle.internal.services;

import org.springframework.stereotype.Component;
import tech.kronicle.plugins.gradle.config.PomCacheConfig;
import tech.kronicle.pluginutils.services.BaseFileCache;
import tech.kronicle.pluginutils.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Component
public class PomCache extends BaseFileCache {

    public PomCache(FileUtils fileUtils, PomCacheConfig config) throws IOException {
        super(fileUtils, Path.of(config.getDir()));
    }

    public Optional<String> get(String url) {
        return getFileContent(url);
    }

    public void put(String url, String jsonContent) {
        putFileContent(url, jsonContent);
    }
}
