package tech.kronicle.plugins.kubernetes.testutils;

import tech.kronicle.plugins.kubernetes.config.ClusterConfig;

public final class ClusterConfigUtils {

    public static ClusterConfig createCluster() {
        return createCluster(1);
    }

    public static ClusterConfig createCluster(
            Boolean apiResourcesWithSupportedMetadataOnly,
            Boolean createContainerStatusChecks
    ) {
        return createCluster(1, apiResourcesWithSupportedMetadataOnly, createContainerStatusChecks);
    }

    public static ClusterConfig createCluster(int clusterNumber) {
        return createCluster(clusterNumber, null, null);
    }

    public static ClusterConfig createCluster(
            int clusterNumber,
            Boolean apiResourcesWithSupportedMetadataOnly,
            Boolean createContainerStatusChecks
    ) {
        return new ClusterConfig(
                "test-environment-id-" + clusterNumber,
                null,
                apiResourcesWithSupportedMetadataOnly,
                createContainerStatusChecks
        );
    }

    private ClusterConfigUtils() {
    }
}
