package tech.kronicle.plugins.gradle.internal.services;

import org.springframework.stereotype.Component;
import tech.kronicle.plugins.gradle.config.UrlExistsCacheConfig;
import tech.kronicle.pluginutils.services.BaseFileCache;
import tech.kronicle.pluginutils.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Component
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
