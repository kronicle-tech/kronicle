package tech.kronicle.plugins.kubernetes.client;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.SneakyThrows;
import tech.kronicle.plugins.kubernetes.config.ClusterConfig;

import java.io.StringReader;

public class ApiClientFactory {

    @SneakyThrows
    public ApiClient createApiClient(ClusterConfig cluster) {
        return ClientBuilder
                .kubeconfig(KubeConfig.loadKubeConfig(new StringReader(unescapeNewlines(cluster.getKubeConfig()))))
                .build();
    }

    private String unescapeNewlines(String value) {
        return value.replace("\\n", "\n");
    }
}
