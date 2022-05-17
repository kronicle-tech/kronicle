package tech.kronicle.service.services;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.service.models.ExtensionOutputCacheKey;

import java.util.function.Supplier;

@Service
public class ExtensionOutputCache {

    private final LoadingCache<ExtensionOutputCacheKey, Output> cache;

    public ExtensionOutputCache(
            ExtensionOutputCacheLoader loader,
            ExtensionOutputCacheExpiry expiry
    ) {
        this.cache = Caffeine.newBuilder()
                .expireAfter(expiry)
                .build(loader);
    }

    public <E, K, I, O, T> Output<O, T> get(E extension, K key, I input, Supplier<Output<O, T>> loader) {
        return (Output<O, T>) cache
                .get(new ExtensionOutputCacheKey<>(
                        extension,
                        key,
                        input,
                        loader
                ));
    }
}
