package tech.kronicle.plugins.aws.xray.client;

import tech.kronicle.plugins.aws.xray.models.XRayServiceGraphPage;

import java.time.Instant;

public interface XRayClientFacade extends AutoCloseable {

    XRayServiceGraphPage getServiceGraph(Instant startTime, Instant endTime, String nextToken);

    @Override
    void close();
}
