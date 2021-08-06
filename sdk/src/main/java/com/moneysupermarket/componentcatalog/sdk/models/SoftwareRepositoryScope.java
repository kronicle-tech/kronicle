package com.moneysupermarket.componentcatalog.sdk.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SoftwareRepositoryScope {

    BUILDSCRIPT;

    @JsonValue
    public String value() {
        return name().replaceAll("_", "-").toLowerCase();
    }
}
