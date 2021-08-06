package com.moneysupermarket.componentcatalog.service.scanners.models;

import com.moneysupermarket.componentcatalog.sdk.models.Component;
import com.moneysupermarket.componentcatalog.sdk.models.ObjectWithReference;
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
