package tech.kronicle.plugins.kubernetes.models;

import lombok.Value;

@Value
public class ApiResource {

    String kind;
    String group;
    String preferredVersion;
    String resourcePlural;
}
