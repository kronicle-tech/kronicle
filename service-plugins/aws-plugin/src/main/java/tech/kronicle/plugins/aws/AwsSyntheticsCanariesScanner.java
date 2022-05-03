package tech.kronicle.plugins.aws;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.LogSummaryStateAndContext;
import tech.kronicle.plugins.aws.synthetics.models.CheckStateAndContext;
import tech.kronicle.plugins.aws.synthetics.services.SyntheticsService;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.LogSummaryState;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toUnmodifiableList;

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
        List<CheckStateAndContext> checks =
                service.getCanaryLastRunsForComponent(input);

        if (checks.isEmpty()) {
            return Output.ofTransformer(null, CACHE_TTL);
        }

        return Output.ofTransformer(updateComponentState(checks), CACHE_TTL);
    }

    private UnaryOperator<Component> updateComponentState(List<CheckStateAndContext> checks) {
        return component -> component.withUpdatedState(state -> {
            Map<String, List<CheckState>> checksByEnvironmentId = checks.stream()
                    .collect(groupingBy(
                            CheckStateAndContext::getEnvironmentId,
                            mapping(CheckStateAndContext::getCheck, toUnmodifiableList())
                    ));
            for (Map.Entry<String, List<CheckState>> entry : checksByEnvironmentId.entrySet()) {
                state = state.withUpdatedEnvironment(
                        entry.getKey(),
                        environment -> environment.withUpdatedPlugin(
                                AwsPlugin.ID,
                                plugin -> plugin.addChecks(entry.getValue())
                        )
                );
            }
            return state;
        });
    }
}
