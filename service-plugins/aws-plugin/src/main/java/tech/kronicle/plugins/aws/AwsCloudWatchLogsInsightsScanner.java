package tech.kronicle.plugins.aws;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.cloudwatchlogs.services.CloudWatchLogsService;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.ComponentStateLogSummary;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AwsCloudWatchLogsInsightsScanner extends ComponentScanner {

    private final CloudWatchLogsService service;

    @Override
    public String description() {
        return "Finds the number of log entries for each log level for a component";
    }

    @Override
    public void refresh(ComponentMetadata componentMetadata) {
        service.refresh();
    }

    @Override
    public Output<Void> scan(Component input) {
        List<Map.Entry<AwsProfileAndRegion, List<ComponentStateLogSummary>>> logLevelCounts =
                service.getLogSummariesForComponent(input);

        if (logLevelCountsIsEmpty(logLevelCounts)) {
            return Output.of(UnaryOperator.identity());
        }

        return Output.of(component -> component.withUpdatedState(state -> {
            for (Map.Entry<AwsProfileAndRegion, List<ComponentStateLogSummary>> entry : logLevelCounts) {
                List<ComponentStateLogSummary> logSummaries = entry.getValue();
                if (!logSummaries.isEmpty()) {
                    String environmentId = entry.getKey().getProfile().getEnvironmentId();
                    state = state.withUpdatedEnvironment(
                            environmentId,
                            environment -> environment.withUpdatedPlugin(
                                    AwsPlugin.ID,
                                    plugin -> plugin.withLogSummaries(logSummaries)
                            )
                    );
                }
            }
            return state;
        }));
    }

    private boolean logLevelCountsIsEmpty(
            List<Map.Entry<AwsProfileAndRegion, List<ComponentStateLogSummary>>> logLevelCounts
    ) {
        return logLevelCounts.stream().allMatch(entry -> entry.getValue().isEmpty());
    }
}
