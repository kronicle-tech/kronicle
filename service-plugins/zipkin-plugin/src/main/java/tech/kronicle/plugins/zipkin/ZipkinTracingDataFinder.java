package tech.kronicle.plugins.zipkin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.TracingDataFinder;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.zipkin.models.api.Service;
import tech.kronicle.plugins.zipkin.models.api.Span;
import tech.kronicle.plugins.zipkin.services.TraceMapper;
import tech.kronicle.plugins.zipkin.services.ZipkinService;
import tech.kronicle.sdk.models.ComponentMetadata;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;

@Extension
@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ZipkinTracingDataFinder extends TracingDataFinder {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final ZipkinService zipkinService;
    private final TraceMapper traceMapper;

    @Override
    public String description() {
        return "Retrieves trace data from Zipkin Server instance using the Zipkin REST API";
    }

    @Override
    public String notes() {
        return "The finder will retrieve all traces from Zipkin up to a configurable limit" +
                " of traces per service.  Kronicle can than use these traces to work out:\n"
                + "\n"
                + "* All the dependencies between the services\n"
                + "* Calculate the response time percentiles for each service and span name combination";
    }

    @Override
    public Output<TracingData, Void> find(ComponentMetadata input) {
        log.info("Getting Zipkin services");
        List<Service> services = zipkinService.getServices();
        log.info("Retrieved {} Zipkin services", services.size());
        log.info("Getting Zipkin traces");
        List<List<Span>> traces = zipkinService.getTraces(services);
        log.info("Retrieved {} Zipkin traces", traces.size());
        log.info("Getting Zipkin component dependencies");

        return Output.ofOutput(
                new TracingData(null, traceMapper.mapTraces(traces)),
                CACHE_TTL
        );
    }
}
