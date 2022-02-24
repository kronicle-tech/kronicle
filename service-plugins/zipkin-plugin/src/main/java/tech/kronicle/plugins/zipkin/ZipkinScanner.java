package tech.kronicle.plugins.zipkin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.plugins.zipkin.config.ZipkinConfig;
import tech.kronicle.plugins.zipkin.models.api.Service;
import tech.kronicle.plugins.zipkin.models.api.Span;
import tech.kronicle.plugins.zipkin.services.CallGraphCollator;
import tech.kronicle.plugins.zipkin.services.ComponentDependencyCollator;
import tech.kronicle.plugins.zipkin.services.SubComponentDependencyCollator;
import tech.kronicle.plugins.zipkin.services.ZipkinService;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.Summary;
import tech.kronicle.sdk.models.SummaryCallGraph;
import tech.kronicle.sdk.models.SummaryComponentDependencies;
import tech.kronicle.sdk.models.SummarySubComponentDependencies;
import tech.kronicle.sdk.models.zipkin.Zipkin;
import tech.kronicle.sdk.models.zipkin.ZipkinDependency;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.models.Output;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Extension
@org.springframework.stereotype.Component
@Slf4j
@RequiredArgsConstructor
public class ZipkinScanner extends ComponentScanner {

    private final ZipkinConfig config;
    private final ZipkinService zipkinService;
    private final ComponentDependencyCollator componentDependencyCollator;
    private final SubComponentDependencyCollator subComponentDependencyCollator;
    private final CallGraphCollator callGraphCollator;

    private List<ZipkinDependency> dependencies;
    private SummaryComponentDependencies componentDependencies;
    private SummarySubComponentDependencies subComponentDependencies;
    private List<SummaryCallGraph> callGraphs;

    @Override
    public String id() {
        return "zipkin";
    }

    @Override
    public String description() {
        return "Scans a Zipkin Server instance using its Zipkin API endpoints";
    }

    @Override
    public String notes() {
        return "The scanner will retrieve all the names of the services in Zipkin and the names of all the spans belonging to those services.  It will "
                + "retrieve a sample of traces for each of the combinations of service name and span name and then uses the combination of all those traces "
                + "to work out:\n"
                + "\n"
                + "* All the dependencies between the services\n"
                + "* Calculate the response time percentices for each service and span name combination";
    }

    @Override
    public void refresh(ComponentMetadata componentMetadata, List<Dependency> dependencies) {
        this.dependencies = List.of();
        List<List<Span>> traces = List.of();

        if (config.getEnabled()) {
            try {
                log.info("Getting Zipkin dependencies");
                this.dependencies = zipkinService.getDependencies();
                log.info("Retrieved {} Zipkin dependencies", this.dependencies.size());
                log.info("Getting Zipkin services");
                List<Service> services = zipkinService.getServices();
                log.info("Retrieved {} Zipkin services", services.size());
                log.info("Getting Zipkin traces");
                traces = zipkinService.getTraces(services);
                log.info("Retrieved {} Zipkin traces", traces.size());
                log.info("Getting Zipkin component dependencies");
            } catch (Exception e) {
                log.error("Could not fetch information from Zipkin", e);
            }
        }

        componentDependencies = componentDependencyCollator.collateDependencies(traces, dependencies);
        log.info("Retrieved {} Zipkin component dependencies", componentDependencies.getDependencies().size());
        log.info("Getting Zipkin sub-component dependencies");
        subComponentDependencies = subComponentDependencyCollator.collateDependencies(traces);
        log.info("Retrieved {} Zipkin sub-component dependencies", subComponentDependencies.getDependencies().size());
        log.info("Getting Zipkin call graphs");
        callGraphs = callGraphCollator.collateCallGraphs(traces);
        log.info("Retrieved {} Zipkin call graphs", callGraphs.size());
    }

    @Override
    public Output<Void> scan(Component input) {
        String serviceName = input.getId();
        Zipkin zipkin = Optional.ofNullable(serviceName).map(this::getComponentDataByServiceName).orElse(new Zipkin(serviceName, false, null, null));
        return Output.of(component -> component.withZipkin(zipkin));
    }

    @Override
    public Summary transformSummary(Summary summary) {
        return summary.withComponentDependencies(componentDependencies)
                .withSubComponentDependencies(subComponentDependencies)
                .withCallGraphs(callGraphs);
    }

    private Zipkin getComponentDataByServiceName(String serviceName) {
        if (isNull(dependencies)) {
            return null;
        }

        List<@Valid ZipkinDependency> upstream = getServiceDependencies(ZipkinDependency::getChild, serviceName);
        List<@Valid ZipkinDependency> downstream = getServiceDependencies(ZipkinDependency::getParent, serviceName);
        return new Zipkin(serviceName, !upstream.isEmpty() || !downstream.isEmpty(), upstream, downstream);
    }

    private List<@Valid ZipkinDependency> getServiceDependencies(Function<ZipkinDependency, String> nodeGetter, String serviceName) {
        return dependencies.stream()
                .filter(dependency -> nodeGetter.apply(dependency).equals(serviceName))
                .collect(Collectors.toList());
    }
}
