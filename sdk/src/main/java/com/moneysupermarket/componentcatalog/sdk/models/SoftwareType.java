package com.moneysupermarket.componentcatalog.sdk.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SoftwareType {

    GRADLE_PLUGIN,
    JVM,
    TOOL;

    @JsonValue
    public String value() {
        return name().replaceAll("_", "-").toLowerCase();
    }
}
