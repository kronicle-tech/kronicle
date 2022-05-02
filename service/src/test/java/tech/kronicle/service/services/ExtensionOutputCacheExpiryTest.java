package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.service.models.ExtensionOutputCacheKey;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtensionOutputCacheExpiryTest {

    public static final Duration CACHE_TTL = Duration.ofMinutes(15);
    public static final Output<String, Void> OUTPUT = Output.ofOutput("test-output", CACHE_TTL);
    private static final long CURRENT_TIME = 1234;
    private static final long CURRENT_DURATION = 900000000000L;

    @Test
    public void expireAfterCreateShouldReturnTheCacheTtlOfTheCacheValueInNanoseconds() {
        // Given
        Duration cacheTtl = Duration.ofMinutes(15);
        Output<String, Void> output = Output.ofOutput("test-output", cacheTtl);
        ExtensionOutputCacheExpiry underTest = new ExtensionOutputCacheExpiry();

        // When
        long returnValue = underTest.expireAfterCreate(
                new ExtensionOutputCacheKey<>(
                        new Object(),
                        "test-input",
                        () -> output
                ),
                output,
                CURRENT_TIME
        );

        assertThat(returnValue).isEqualTo(CURRENT_DURATION);
    }

    @Test
    public void expireAfterUpdateShouldNotChangeTheCacheDuration() {
        // Given
        ExtensionOutputCacheExpiry underTest = new ExtensionOutputCacheExpiry();

        // When
        long returnValue = underTest.expireAfterUpdate(
                new ExtensionOutputCacheKey<>(
                        new Object(),
                        "test-input",
                        () -> OUTPUT
                ),
                OUTPUT,
                CURRENT_TIME,
                CURRENT_DURATION
        );

        assertThat(returnValue).isEqualTo(CURRENT_DURATION);
    }

    @Test
    public void expireAfterReadShouldNotChangeTheCacheDuration() {
        // Given
        ExtensionOutputCacheExpiry underTest = new ExtensionOutputCacheExpiry();

        // When
        long returnValue = underTest.expireAfterRead(
                new ExtensionOutputCacheKey<>(
                        new Object(),
                        "test-input",
                        () -> OUTPUT
                ),
                OUTPUT,
                CURRENT_TIME,
                CURRENT_DURATION
        );

        assertThat(returnValue).isEqualTo(CURRENT_DURATION);
    }
}
