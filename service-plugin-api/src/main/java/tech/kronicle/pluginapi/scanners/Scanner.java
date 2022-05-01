package tech.kronicle.pluginapi.scanners;

import tech.kronicle.common.CaseUtils;
import tech.kronicle.pluginapi.ExtensionPointWithId;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.ObjectWithReference;
import tech.kronicle.sdk.models.Summary;

import java.time.Duration;

public abstract class Scanner<I extends ObjectWithReference, O> implements ExtensionPointWithId {

    public String id() {
        return CaseUtils.toKebabCase(getClass().getSimpleName()).replaceFirst("-scanner$", "");
    }

    public abstract String description();

    public String notes() {
        return null;
    }

    public Duration errorCacheTtl() {
        return Duration.ofMinutes(15);
    }

    public void refresh(ComponentMetadata componentMetadata) {
    }

    public abstract Output<O, Component> scan(I input);

    public Summary transformSummary(Summary summary) {
        return summary;
    }
}
