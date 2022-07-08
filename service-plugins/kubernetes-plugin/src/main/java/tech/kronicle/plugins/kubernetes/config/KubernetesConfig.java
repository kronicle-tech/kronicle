package tech.kronicle.plugins.kubernetes.config;

import lombok.Value;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
public class KubernetesConfig {

    List<ClusterConfig> clusters;

    public KubernetesConfig(List<ClusterConfig> clusters) {
        this.clusters = createUnmodifiableList(clusters);
    }
}
