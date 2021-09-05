package tech.kronicle.sdk.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TestOutcome {

    FAIL,
    NOT_APPLICABLE,
    PASS;

    @JsonValue
    public String value() {
        return name().replaceAll("_", "-").toLowerCase();
    }
}
