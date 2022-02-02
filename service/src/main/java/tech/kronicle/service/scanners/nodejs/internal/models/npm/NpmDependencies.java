package tech.kronicle.service.scanners.nodejs.internal.models.npm;

import java.util.Map;

public interface NpmDependencies {

    Map<String, NpmDependency> getDependencies();
}
