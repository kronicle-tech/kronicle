package tech.kronicle.plugins.aws.xray.services;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.GetServiceGraphRequest;
import software.amazon.awssdk.services.xray.model.GetServiceGraphResponse;
import tech.kronicle.plugins.aws.xray.models.Alias;
import tech.kronicle.plugins.aws.xray.models.Edge;
import tech.kronicle.plugins.aws.xray.models.Service;
import tech.kronicle.plugins.aws.xray.models.ServiceGraphPage;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class XRayClientFacade {

    private XRayClient client;

    public ServiceGraphPage getServiceGraph(Instant startTime, Instant endTime, String nextToken) {
        GetServiceGraphResponse serviceGraph = client.getServiceGraph(GetServiceGraphRequest.builder()
                .startTime(startTime)
                .endTime(endTime)
                .nextToken(nextToken)
                .build());
        return new ServiceGraphPage(
                mapServices(serviceGraph.services()),
                serviceGraph.nextToken()
        );
    }

    private List<Service> mapServices(List<software.amazon.awssdk.services.xray.model.Service> services) {
        return services.stream()
                .map(service -> new Service(service.name(), service.names(), mapEdges(service.edges())))
                .collect(Collectors.toList());
    }

    private List<Edge> mapEdges(List<software.amazon.awssdk.services.xray.model.Edge> edges) {
        return edges.stream()
                .map(edge -> new Edge(mapAliases(edge.aliases())))
                .collect(Collectors.toList());
    }

    private List<Alias> mapAliases(List<software.amazon.awssdk.services.xray.model.Alias> aliases) {
        return aliases.stream()
                .map(alias -> new Alias(alias.name(), alias.names()))
                .collect(Collectors.toList());
    }
}
