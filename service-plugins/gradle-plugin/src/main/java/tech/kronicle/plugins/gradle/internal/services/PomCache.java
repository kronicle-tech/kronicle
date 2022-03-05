package tech.kronicle.plugins.gradle.internal.services;

import tech.kronicle.plugins.gradle.config.PomCacheConfig;
import tech.kronicle.pluginutils.BaseFileCache;
import tech.kronicle.pluginutils.FileUtils;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.Optional;

public class PomCache extends BaseFileCache {

    @Inject
    public PomCache(FileUtils fileUtils, PomCacheConfig config) {
        super(fileUtils, Path.of(config.getDir()));
    }

    public Optional<String> get(String url) {
        return getFileContent(url);
    }

    public void put(String url, String jsonContent) {
        putFileContent(url, jsonContent);
    }
}
