package tech.kronicle.service.scanners.nodejs.internal.models.npm;

import lombok.Value;

import java.util.Map;

@Value
public class NpmDependency implements NpmDependencies {

    String version;
    Boolean dev;
    Map<String, NpmDependency> dependencies;

}
