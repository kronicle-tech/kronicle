package tech.kronicle.service.services;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.service.models.ExtensionOutputCacheKey;

@Service
public class ExtensionOutputCacheLoader implements CacheLoader<ExtensionOutputCacheKey, Output> {

    @Nullable
    @Override
    public Output load(ExtensionOutputCacheKey key) {
        return (Output) key.getLoader().get();
    }
}
