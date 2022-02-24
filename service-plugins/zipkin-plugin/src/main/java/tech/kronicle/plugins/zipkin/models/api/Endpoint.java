package tech.kronicle.plugins.zipkin.models.api;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Endpoint {

    String serviceName;
    String ipv4;
    String ipv6;
    Integer port;
}
