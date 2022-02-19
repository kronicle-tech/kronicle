package tech.kronicle.plugins.nodejs.internal.models.npm;

import java.util.Map;

public interface NpmDependencies {

    Map<String, NpmDependency> getDependencies();
}
