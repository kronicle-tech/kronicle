package tech.kronicle.plugins.aws;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.synthetics.services.SyntheticsService;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AwsSyntheticsCanariesScanner extends ComponentScanner {

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
    public Output<Void> scan(Component input) {
        List<Map.Entry<AwsProfileAndRegion, List<CheckState>>> checks =
                service.getCanaryLastRunsForComponent(input);

        if (checksIsEmpty(checks)) {
            return Output.of(UnaryOperator.identity());
        }

        return Output.of(component -> component.withUpdatedState(state -> {
            for (Map.Entry<AwsProfileAndRegion, List<CheckState>> entry : checks) {
                List<CheckState> checksForProfileAndRegion = entry.getValue();
                if (!checksForProfileAndRegion.isEmpty()) {
                    String environmentId = entry.getKey().getProfile().getEnvironmentId();
                    state = state.withUpdatedEnvironment(
                            environmentId,
                            environment -> environment.withUpdatedPlugin(
                                    AwsPlugin.ID,
                                    plugin -> plugin.withChecks(checksForProfileAndRegion)
                            )
                    );
                }
            }
            return state;
        }));
    }

    private boolean checksIsEmpty(
            List<Map.Entry<AwsProfileAndRegion, List<CheckState>>> checks
    ) {
        return checks.stream().allMatch(entry -> entry.getValue().isEmpty());
    }
}
