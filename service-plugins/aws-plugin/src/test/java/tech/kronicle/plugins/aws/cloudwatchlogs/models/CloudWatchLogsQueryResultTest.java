package tech.kronicle.plugins.aws.cloudwatchlogs.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class CloudWatchLogsQueryResultTest {

    @Test
    public void constructorShouldMakeFieldsAnUnmodifiableList() {
        // Given
        CloudWatchLogsQueryResult underTest = new CloudWatchLogsQueryResult(new ArrayList<>());

        // When
        Throwable thrown = catchThrowable(() -> underTest.getFields().add(new CloudWatchLogsQueryResultField("", "")));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
