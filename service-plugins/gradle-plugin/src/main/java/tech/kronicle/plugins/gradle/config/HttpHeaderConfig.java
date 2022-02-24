package tech.kronicle.plugins.gradle.config;

import lombok.Value;

@Value
public class HttpHeaderConfig {

    String name;
    String value;
}
