package tech.kronicle.plugins.kubernetes.testutils;

import tech.kronicle.plugins.kubernetes.config.ClusterConfig;

public final class ClusterConfigUtils {

    public static ClusterConfig createCluster() {
        return createCluster(1);
    }

    public static ClusterConfig createCluster(Boolean apiResourcesWithSupportedMetadataOnly) {
        return createCluster(1, apiResourcesWithSupportedMetadataOnly);
    }

    public static ClusterConfig createCluster(int clusterNumber) {
        return createCluster(clusterNumber, null);
    }

    public static ClusterConfig createCluster(int clusterNumber, Boolean apiResourcesWithSupportedMetadataOnly) {
        return new ClusterConfig(
                "test-environment-id-" + clusterNumber,
                null,
                apiResourcesWithSupportedMetadataOnly
        );
    }

    private ClusterConfigUtils() {
    }
}
