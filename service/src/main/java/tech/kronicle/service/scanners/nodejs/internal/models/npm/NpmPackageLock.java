package tech.kronicle.service.scanners.nodejs.internal.models.npm;

import lombok.Value;

import java.util.Map;

@Value
public class NpmPackageLock implements NpmDependencies {

    Map<String, NpmDependency> dependencies;
}
