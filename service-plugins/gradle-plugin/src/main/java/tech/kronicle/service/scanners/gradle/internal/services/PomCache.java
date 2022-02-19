package tech.kronicle.service.scanners.gradle.internal.services;

import tech.kronicle.service.scanners.gradle.config.GradleConfig;
import tech.kronicle.service.services.BaseFileCache;
import tech.kronicle.service.spring.stereotypes.SpringComponent;
import tech.kronicle.service.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@SpringComponent
public class PomCache extends BaseFileCache {

    public PomCache(FileUtils fileUtils, GradleConfig config) throws IOException {
        super(fileUtils, Path.of(config.getPomCache().getDir()));
    }

    public Optional<String> get(String url) {
        return getFileContent(url);
    }

    public void put(String url, String jsonContent) {
        putFileContent(url, jsonContent);
    }
}
