package tech.kronicle.plugins.gradle.internal.services;

import org.springframework.stereotype.Component;
import tech.kronicle.plugins.gradle.config.DownloadCacheConfig;
import tech.kronicle.pluginutils.services.BaseFileCache;
import tech.kronicle.pluginutils.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Component
public class DownloadCache extends BaseFileCache {

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
