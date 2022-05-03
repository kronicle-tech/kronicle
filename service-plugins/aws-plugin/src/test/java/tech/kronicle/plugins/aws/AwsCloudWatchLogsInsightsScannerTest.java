package tech.kronicle.plugins.aws;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.LogSummaryStateAndContext;
import tech.kronicle.plugins.aws.cloudwatchlogs.services.CloudWatchLogsService;
import tech.kronicle.plugintestutils.scanners.BaseScannerTest;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.EnvironmentPluginState;
import tech.kronicle.sdk.models.EnvironmentState;
import tech.kronicle.sdk.models.LogSummaryState;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.aws.testutils.ComponentUtils.createComponent;

public class AwsCloudWatchLogsInsightsScannerTest extends BaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // Given
        AwsCloudWatchLogsInsightsScanner underTest = new AwsCloudWatchLogsInsightsScanner(null);

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("aws-cloud-watch-logs-insights");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // Given
        AwsCloudWatchLogsInsightsScanner underTest = new AwsCloudWatchLogsInsightsScanner(null);

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo(
                "Finds the number of log entries and top log messages for each log level for a component"
        );
    }

    @Test
    public void notesShouldReturnNull() {
        // Given
        AwsCloudWatchLogsInsightsScanner underTest = new AwsCloudWatchLogsInsightsScanner(null);

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void refreshShouldRefreshTheService() {
        // Given
        CloudWatchLogsService service = mock(CloudWatchLogsService.class);
        AwsCloudWatchLogsInsightsScanner underTest = new AwsCloudWatchLogsInsightsScanner(service);

        // When
        underTest.refresh(ComponentMetadata.builder().build());

        // Then
        verify(service).refresh();
    }

    @Test
    public void scanShouldAddLogSummariesToTheComponent() {
        // Given
        CloudWatchLogsService service = mock(CloudWatchLogsService.class);
        AwsCloudWatchLogsInsightsScanner underTest = new AwsCloudWatchLogsInsightsScanner(service);
        Component component = createComponent(1);
        when(service.getLogSummariesForComponent(component)).thenReturn(List.of(
                createLogSummaryAndContext(1, 1),
                createLogSummaryAndContext(1, 2),
                createLogSummaryAndContext(2, 1),
                createLogSummaryAndContext(2, 2)
        ));

        // When
        Output<Void, Component> returnValue = underTest.scan(component);

        // Then
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getErrors()).isEmpty();
        assertThat(returnValue.getCacheTtl()).isEqualTo(CACHE_TTL);
        Component transformedComponent = getMutatedComponent(returnValue, component);
        assertThat(transformedComponent).isEqualTo(
                component.withState(
                        ComponentState.builder()
                                .environments(List.of(
                                        createEnvironment(1),
                                        createEnvironment(2)
                                ))
                                .build()
                )
        );
    }

    @Test
    public void scanShouldNotTransformTheComponentIfNoLogSummariesAreFound() {
        // Given
        CloudWatchLogsService service = mock(CloudWatchLogsService.class);
        AwsCloudWatchLogsInsightsScanner underTest = new AwsCloudWatchLogsInsightsScanner(service);
        Component component = createComponent(1);
        when(service.getLogSummariesForComponent(component)).thenReturn(List.of());

        // When
        Output<Void, Component> returnValue = underTest.scan(component);

        // Then
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getErrors()).isEmpty();
        assertThat(returnValue.getCacheTtl()).isEqualTo(CACHE_TTL);
        Component transformedComponent = getMutatedComponent(returnValue, component);
        assertThat(transformedComponent).isEqualTo(component);
    }

    private EnvironmentState createEnvironment(int environmentNumber) {
        return EnvironmentState.builder()
                .id("test-environment-id-" + environmentNumber)
                .plugins(List.of(
                        EnvironmentPluginState.builder()
                                .id("aws")
                                .logSummaries(List.of(
                                        LogSummaryState.builder()
                                                .name("test-log-summary-name-" + environmentNumber + "-1")
                                                .build(),
                                        LogSummaryState.builder()
                                                .name("test-log-summary-name-" + environmentNumber + "-2")
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

    private LogSummaryStateAndContext createLogSummaryAndContext(
            int profileAndRegionNumber,
            int logSummaryNumber
    ) {
        return new LogSummaryStateAndContext(
                "test-environment-id-" + profileAndRegionNumber,
                createLogSummary(profileAndRegionNumber, logSummaryNumber)
        );
    }

    private LogSummaryState createLogSummary(
            int profileAndRegionNumber,
            int logSummaryNumber
    ) {
        return LogSummaryState.builder()
                .name("test-log-summary-name-" + profileAndRegionNumber + "-" + logSummaryNumber)
                .build();
    }
}
