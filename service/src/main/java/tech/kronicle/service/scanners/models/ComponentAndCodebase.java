package tech.kronicle.service.scanners.models;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ObjectWithReference;
import lombok.Value;

@Value
public class ComponentAndCodebase implements ObjectWithReference {

    Component component;
    Codebase codebase;

    @Override
    public String reference() {
        return component.reference();
    }
}
