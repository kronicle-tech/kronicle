package tech.kronicle.plugins.aws.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class BaseClientFacade<T extends AutoCloseable> implements AutoCloseable {

    private final ClientFactory<T> clientFactory;
    private final Map<AwsProfileAndRegion, T> clients = new HashMap<>();

    @SneakyThrows
    @Override
    public void close() {
        for (T t : clients.values()) {
            t.close();
        }
    }

    protected T getClient(AwsProfileAndRegion profileAndRegion) {
        return clients.computeIfAbsent(profileAndRegion, clientFactory::createClient);
    }
}
