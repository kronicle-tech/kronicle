package tech.kronicle.sdk.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SoftwareType {

    GRADLE_PLUGIN,
    GRADLE_PLUGIN_VERSION,
    JVM,
    TOOL;

    @JsonValue
    public String value() {
        return name().replaceAll("_", "-").toLowerCase();
    }
}
