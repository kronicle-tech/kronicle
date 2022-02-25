package tech.kronicle.plugins.nodejs.internal.models;

import lombok.Value;

import java.util.Map;

@Value
public class PackageJson {

    Map<String, String> dependencies;
    Map<String, String> devDependencies;
}
