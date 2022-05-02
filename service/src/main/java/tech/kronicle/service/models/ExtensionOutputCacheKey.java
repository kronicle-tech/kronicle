package tech.kronicle.service.models;

import lombok.EqualsAndHashCode;
import lombok.Value;
import tech.kronicle.pluginapi.scanners.models.Output;

import java.util.function.Supplier;

@Value
public class ExtensionOutputCacheKey<E, I, O, T> {

    E extension;
    I input;
    @EqualsAndHashCode.Exclude
    Supplier<Output<O, T>> loader;
}
