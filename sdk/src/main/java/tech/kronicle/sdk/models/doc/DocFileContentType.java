package tech.kronicle.sdk.models.doc;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DocFileContentType {

    BINARY,
    TEXT;

    @JsonValue
    public String value() {
        return name().replaceAll("_", "-").toLowerCase();
    }
}
