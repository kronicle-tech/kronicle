package tech.kronicle.pluginapi.scanners.models;

import lombok.Value;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ObjectWithReference;

@Value
public class ComponentAndCodebase implements ObjectWithReference {

    Component component;
    Codebase codebase;

    @Override
    public String reference() {
        return component.reference();
    }
}
