package tech.kronicle.plugins.aws.testutils;

import tech.kronicle.sdk.models.Dependency;

public final class DependencyUtils {

    public static Dependency createDependency(int dependencyNumber) {
        return Dependency.builder()
                .sourceComponentId("test-source-component-id-" + dependencyNumber + "-1")
                .targetComponentId("test-source-component-id-" + dependencyNumber + "-2")
                .build();
    }

    private DependencyUtils() {
    }
}
