package tech.kronicle.plugins.aws;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AwsCloudWatchLogsInsightsScannerTest {

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
                "Finds the number of log entries for each log level for a component"
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
}
