package tech.kronicle.sdk.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DependencyDirection {

    INBOUND,
    OUTBOUND;

    @JsonValue
    public String value() {
        return name().replaceAll("_", "-").toLowerCase();
    }
}
