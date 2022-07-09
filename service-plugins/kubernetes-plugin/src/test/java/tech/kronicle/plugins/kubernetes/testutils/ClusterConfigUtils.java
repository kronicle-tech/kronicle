package tech.kronicle.plugins.kubernetes.testutils;

import tech.kronicle.plugins.kubernetes.config.ClusterConfig;

public final class ClusterConfigUtils {

    public static ClusterConfig createCluster() {
        return createCluster(1);
    }

    public static ClusterConfig createCluster(int clusterNumber) {
        return new ClusterConfig("test-environment-id-" + clusterNumber, null);
    }

    private ClusterConfigUtils() {
    }
}
