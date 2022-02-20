package tech.kronicle.plugins.gradle.internal.services;

import tech.kronicle.plugins.gradle.config.UrlExistsCacheConfig;
import tech.kronicle.service.services.BaseFileCache;
import tech.kronicle.service.spring.stereotypes.SpringComponent;
import tech.kronicle.service.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@SpringComponent
public class UrlExistsCache extends BaseFileCache {

    public UrlExistsCache(FileUtils fileUtils, UrlExistsCacheConfig config) throws IOException {
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
