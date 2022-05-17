package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.service.models.ExtensionOutputCacheKey;

import java.time.Duration;
import java.util.List;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtensionOutputCacheLoaderTest {

    @Test
    public void loadShouldCallTheLoaderOfTheKeyAndReturnItsValue() {
        // Given
        Output<Integer, Component> output = new Output<>(1234, UnaryOperator.identity(), List.of(), Duration.ofMinutes(15));
        ExtensionOutputCacheKey<Object, String, String, Integer, Component> key = new ExtensionOutputCacheKey<>(
                new Object(),
                "test-key",
                "test-input",
                () -> output
        );
        ExtensionOutputCacheLoader underTest = new ExtensionOutputCacheLoader();

        // When
        Output<?, ?> returnValue = underTest.load(key);

        // Then
        assertThat(returnValue).isEqualTo(output);
    }
}
