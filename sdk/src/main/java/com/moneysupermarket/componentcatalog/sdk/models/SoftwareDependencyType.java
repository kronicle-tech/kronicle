package com.moneysupermarket.componentcatalog.sdk.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SoftwareDependencyType {

    DIRECT,
    TRANSITIVE;

    @JsonValue
    public String value() {
        return name().replaceAll("_", "-").toLowerCase();
    }
}
