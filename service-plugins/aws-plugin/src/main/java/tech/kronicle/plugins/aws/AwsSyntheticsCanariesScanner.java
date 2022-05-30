package tech.kronicle.plugins.aws;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.synthetics.services.SyntheticsService;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AwsSyntheticsCanariesScanner extends ComponentScanner {

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    private final SyntheticsService service;

    @Override
    public String description() {
        return "Finds AWS CloudWatch Synthetics Canaries and adds the state of those canaries to components";
    }

    @Override
    public void refresh(ComponentMetadata componentMetadata) {
        service.refresh();
    }

    @Override
    public Output<Void, Component> scan(Component input) {
        List<CheckState> checks = service.getCanaryLastRunsForComponent(input);

        if (checks.isEmpty()) {
            return Output.ofTransformer(null, CACHE_TTL);
        }

        return Output.ofTransformer(
                component -> component.addStates(List.copyOf(checks)),
                CACHE_TTL
        );
    }
}
