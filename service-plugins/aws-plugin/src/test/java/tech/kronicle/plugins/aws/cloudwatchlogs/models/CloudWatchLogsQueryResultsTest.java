package tech.kronicle.plugins.aws.cloudwatchlogs.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class CloudWatchLogsQueryResultsTest {

    @Test
    public void constructorShouldMakeResultsAnUnmodifiableList() {
        // Given
        CloudWatchLogsQueryResults underTest = new CloudWatchLogsQueryResults("", new ArrayList<>());

        // When
        Throwable thrown = catchThrowable(() -> underTest.getResults().add(new CloudWatchLogsQueryResult(List.of())));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
