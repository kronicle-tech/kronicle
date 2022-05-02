package tech.kronicle.service.services;

import com.github.benmanes.caffeine.cache.Expiry;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.service.models.ExtensionOutputCacheKey;

@Service
public class ExtensionOutputCacheExpiry implements Expiry<ExtensionOutputCacheKey<?, ?, ?, ?>, Output<?, ?>> {

    @Override
    public long expireAfterCreate(
            ExtensionOutputCacheKey<?, ?, ?, ?> key,
            Output<?, ?> value,
            long currentTime
    ) {
        return currentTime + value.getCacheTtl().toNanos();
    }

    @Override
    public long expireAfterUpdate(
            ExtensionOutputCacheKey<?, ?, ?, ?> key,
            Output<?, ?> value,
            long currentTime,
            @NonNegative long currentDuration
    ) {
        return currentDuration;
    }

    @Override
    public long expireAfterRead(
            ExtensionOutputCacheKey<?, ?, ?, ?> key,
            Output<?, ?> value,
            long currentTime,
            @NonNegative long currentDuration
    ) {
        return currentDuration;
    }
}
