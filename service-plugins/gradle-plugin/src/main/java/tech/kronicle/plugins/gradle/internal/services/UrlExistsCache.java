package tech.kronicle.plugins.gradle.internal.services;

import tech.kronicle.plugins.gradle.config.UrlExistsCacheConfig;
import tech.kronicle.utils.BaseFileCache;
import tech.kronicle.utils.FileUtils;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.Optional;

public class UrlExistsCache extends BaseFileCache {

    @Inject
    public UrlExistsCache(FileUtils fileUtils, UrlExistsCacheConfig config) {
        super(fileUtils, Path.of(config.getDir()));
    }

    public Optional<Boolean> getExists(String url) {
        return getFileContent(url)
                .map(Boolean::parseBoolean);
    }

    public void putExists(String url, boolean exists) {
        putFileContent(url, Boolean.toString(exists));
    }
}
