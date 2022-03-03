package tech.kronicle.plugins.gradle.internal.services;

import tech.kronicle.plugins.gradle.config.DownloadCacheConfig;
import tech.kronicle.pluginutils.BaseFileCache;
import tech.kronicle.pluginutils.FileUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class DownloadCache extends BaseFileCache {

    @Inject
    public DownloadCache(FileUtils fileUtils, DownloadCacheConfig config) throws IOException {
        super(fileUtils, Path.of(config.getDir()));
    }

    public Optional<String> getContent(String url) {
        return getFileContent(url);
    }

    public void putContent(String url, String content) {
        putFileContent(url, content);
    }
}
