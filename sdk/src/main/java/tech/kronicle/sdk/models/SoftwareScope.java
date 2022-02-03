package tech.kronicle.sdk.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SoftwareScope {

    BUILDSCRIPT,
    DEV;

    @JsonValue
    public String value() {
        return name().replaceAll("_", "-").toLowerCase();
    }
}
