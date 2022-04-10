package tech.kronicle.sdk.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ComponentStateCheckStatus {

    OK,
    WARNING,
    CRITICAL,
    UNKNOWN;

    @JsonValue
    public String value() {
        return name().replaceAll("_", "-").toLowerCase();
    }
}
