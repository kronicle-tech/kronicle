package tech.kronicle.plugins.gradle.internal.models;

import lombok.Value;

@Value
public class Import {

    String className;
    String aliasName;
}
