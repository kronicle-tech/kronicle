package tech.kronicle.service.scanners.gradle.internal.services;

import tech.kronicle.service.scanners.gradle.config.DownloadCacheConfig;
import tech.kronicle.service.services.BaseFileCache;
import tech.kronicle.service.spring.stereotypes.SpringComponent;
import tech.kronicle.service.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@SpringComponent
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
