package com.moneysupermarket.componentcatalog.service.services;

import com.moneysupermarket.componentcatalog.service.config.UrlExistsCacheConfig;
import com.moneysupermarket.componentcatalog.service.utils.FileUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Service
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
