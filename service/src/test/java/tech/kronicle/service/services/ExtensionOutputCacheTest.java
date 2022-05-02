package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.service.models.ExtensionOutputCacheKey;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtensionOutputCacheTest {

    public static final String INPUT = "test-input";
    public static final Object EXTENSION = new Object();
    public static final Output<Integer, Component> OUTPUT = createOutput(1);

    private final FakeOutputLoader outputLoader = new FakeOutputLoader(1);

    @Test
    public void getShouldCallTheOutputLoaderTheFirstTimeItIsCalledForAParticularKey() {
        // Given
        ExtensionOutputCacheKey<Object, String, Integer, Component> key = new ExtensionOutputCacheKey<>(
                EXTENSION,
                INPUT,
                outputLoader
        );
        ExtensionOutputCache underTest = createUnderTest();

        // When
        Output<Integer, Component> returnValue = underTest.get(
                EXTENSION,
                INPUT,
                outputLoader
        );

        // Then
        assertThat(returnValue).isEqualTo(OUTPUT);
        assertThat(outputLoader.callCount).isEqualTo(1);
    }

    @Test
    public void getShouldCallTheOutputLoaderOnlyOnceForAParticularKey() {
        // Given
        ExtensionOutputCacheKey<Object, String, Integer, Component> key = new ExtensionOutputCacheKey<>(
                EXTENSION,
                INPUT,
                outputLoader
        );
        ExtensionOutputCache underTest = createUnderTest();

        // When
        Output<Integer, Component> returnValue = underTest.get(
                EXTENSION,
                INPUT,
                outputLoader
        );

        // Then
        assertThat(returnValue).isEqualTo(OUTPUT);
        assertThat(outputLoader.callCount).isEqualTo(1);

        // When
        returnValue = underTest.get(
                EXTENSION,
                INPUT,
                outputLoader
        );

        // Then
        assertThat(returnValue).isEqualTo(OUTPUT);
        assertThat(outputLoader.callCount).isEqualTo(1);
    }

    private ExtensionOutputCache createUnderTest() {
        return new ExtensionOutputCache(
                new ExtensionOutputCacheLoader(),
                new ExtensionOutputCacheExpiry()
        );
    }

    private static Output<Integer, Component> createOutput(int outputNumber) {
        return new Output<>(outputNumber, UnaryOperator.identity(), List.of(), Duration.ofMinutes(outputNumber));
    }

    @RequiredArgsConstructor
    private static class FakeOutputLoader implements Supplier<Output<Integer, Component>> {

        private final int outputNumber;
        private int callCount;

        @Override
        public Output<Integer, Component> get() {
            callCount++;
            return new Output<>(outputNumber, UnaryOperator.identity(), List.of(), Duration.ofMinutes(outputNumber));
        }
    }
}
