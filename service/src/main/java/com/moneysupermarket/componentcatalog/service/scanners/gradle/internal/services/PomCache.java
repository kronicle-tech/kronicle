package com.moneysupermarket.componentcatalog.service.scanners.gradle.internal.services;

import com.moneysupermarket.componentcatalog.service.scanners.gradle.config.GradleConfig;
import com.moneysupermarket.componentcatalog.service.services.BaseFileCache;
import com.moneysupermarket.componentcatalog.service.utils.FileUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class PomCache extends BaseFileCache {

    public PomCache(FileUtils fileUtils, GradleConfig config) throws IOException {
        super(fileUtils, Path.of(config.getPomCacheDir()));
    }

    public Optional<String> get(String url) {
        return getFileContent(url);
    }

    public void put(String url, String jsonContent) {
        putFileContent(url, jsonContent);
    }
}
