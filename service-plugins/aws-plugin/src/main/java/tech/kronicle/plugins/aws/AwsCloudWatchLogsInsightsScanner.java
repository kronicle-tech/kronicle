package tech.kronicle.plugins.aws;

import lombok.RequiredArgsConstructor;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.ComponentScanner;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.cloudwatchlogs.services.CloudWatchLogsService;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.LogSummaryState;

import javax.inject.Inject;
import java.time.Duration;
import java.util.List;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AwsCloudWatchLogsInsightsScanner extends ComponentScanner {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    private final CloudWatchLogsService service;

    @Override
    public String description() {
        return "Finds the number of log entries and top log messages for each log level for a component";
    }

    @Override
    public void refresh(ComponentMetadata componentMetadata) {
        service.refresh();
    }

    @Override
    public Output<Void, Component> scan(Component input) {
        List<LogSummaryState> logSummaries = service.getLogSummariesForComponent(input);

        if (logSummaries.isEmpty()) {
            return Output.empty(CACHE_TTL);
        }

        return Output.ofTransformer(
                component -> component.addStates(List.copyOf(logSummaries)),
                CACHE_TTL
        );
    }
}
