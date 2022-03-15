package tech.kronicle.plugins.aws.xray.client;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.GetServiceGraphResponse;
import software.amazon.awssdk.services.xray.model.Service;
import tech.kronicle.plugins.aws.xray.models.XRayDependency;
import tech.kronicle.plugins.aws.xray.models.XRayServiceGraphPage;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class XRayClientFacadeImpl implements XRayClientFacade {

    private final XRayClient client;

    @Override
    public void close() {
        client.close();
    }

    public XRayServiceGraphPage getServiceGraph(Instant startTime, Instant endTime, String nextToken) {
        GetServiceGraphResponse serviceGraph = client.getServiceGraph(builder -> builder
                .startTime(startTime)
                .endTime(endTime)
                .nextToken(nextToken));
        return new XRayServiceGraphPage(
                mapServices(serviceGraph.services()),
                serviceGraph.nextToken()
        );
    }

    private List<XRayDependency> mapServices(List<Service> services) {
        Map<Integer, Service> serviceMap = getServiceMap(services);
        return services.stream()
                .flatMap(service -> mapEdges(service, serviceMap))
                .collect(Collectors.toList());
    }

    private Map<Integer, Service> getServiceMap(List<Service> services) {
        return services.stream()
                .collect(Collectors.toMap(Service::referenceId, Function.identity()));
    }

    private Stream<XRayDependency> mapEdges(Service service, Map<Integer, Service> serviceMap) {
        return service.edges().stream().map(edge -> new XRayDependency(
                service.names(),
                serviceMap.get(edge.referenceId()).names())
        );
    }
}
