package tech.kronicle.sdk.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ImportType {

    JAVA;

    @JsonValue
    public String value() {
        return name().replaceAll("_", "-").toLowerCase();
    }
}
