package com.moneysupermarket.componentcatalog.service.services;

import com.moneysupermarket.componentcatalog.service.config.DownloadCacheConfig;
import com.moneysupermarket.componentcatalog.service.utils.FileUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Service
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
