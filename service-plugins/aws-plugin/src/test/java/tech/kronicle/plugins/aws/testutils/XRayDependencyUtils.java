package tech.kronicle.plugins.aws.testutils;

import tech.kronicle.plugins.aws.xray.models.XRayDependency;

import java.util.List;

public final class XRayDependencyUtils {

    public static XRayDependency createXrayDependency(int dependencyNumber) {
        return new XRayDependency(
                List.of(
                        createServiceName(dependencyNumber, 1),
                        createServiceName(dependencyNumber, 2)
                ),
                List.of(
                        createServiceName(dependencyNumber, 3),
                        createServiceName(dependencyNumber, 4)
                )
        );
    }

    private static String createServiceName(int dependencyNumber, int serviceNameNumber) {
        return "test-dependency-" + dependencyNumber + "-" + serviceNameNumber;
    }

    private XRayDependencyUtils() {
    }
}
