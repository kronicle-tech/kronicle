package tech.kronicle.plugins.aws;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.aws.cloudwatchlogs.services.CloudWatchLogsService;
import tech.kronicle.plugintestutils.scanners.BaseScannerTest;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.LogSummary;
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
        LogSummaryState logSummaryState1 = createLogSummaryState(1);
        LogSummaryState logSummaryState2 = createLogSummaryState(2);
        LogSummaryState logSummaryState3 = createLogSummaryState(3);
        LogSummaryState logSummaryState4 = createLogSummaryState(4);
        when(service.getLogSummariesForComponent(component)).thenReturn(List.of(
                logSummaryState1,
                logSummaryState2,
                logSummaryState3,
                logSummaryState4
        ));

        // When
        Output<Void, Component> returnValue = underTest.scan(component);

        // Then
        assertThat(returnValue.getOutput()).isNull();
        assertThat(returnValue.getErrors()).isEmpty();
        assertThat(returnValue.getCacheTtl()).isEqualTo(CACHE_TTL);
        Component transformedComponent = getMutatedComponent(returnValue, component);
        assertThat(transformedComponent).isEqualTo(
                component.withStates(List.of(
                        logSummaryState1,
                        logSummaryState2,
                        logSummaryState3,
                        logSummaryState4
                ))
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

    private LogSummaryState createLogSummaryState(int logSummaryStateNumber) {
        return LogSummaryState.builder()
                .environmentId(createEnvironmentId(logSummaryStateNumber))
                .pluginId("aws")
                .name(createLogSummaryName(logSummaryStateNumber, 1))
                .comparisons(List.of(
                        createLogSummary(logSummaryStateNumber, 2),
                        createLogSummary(logSummaryStateNumber, 3)
                ))
                .build();
    }

    private LogSummary createLogSummary(int logSummaryStateNumber, int logSummaryNumber) {
        return LogSummary.builder()
                .name(createLogSummaryName(logSummaryStateNumber, logSummaryNumber))
                .build();
    }

    private String createEnvironmentId(int logSummaryStateNumber) {
        return "test-environment-id-" + logSummaryStateNumber;
    }

    private String createLogSummaryName(int logSummaryStateNumber, int logSummaryNumber) {
        return "test-log-summary-name-" + logSummaryStateNumber + "-" + logSummaryNumber;
    }
}
