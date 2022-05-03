package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class EnvironmentPluginStateTest {

    @Test
    public void constructorShouldMakeChecksAnUnmodifiableList() {
        // Given
        EnvironmentPluginState underTest = EnvironmentPluginState.builder()
                .checks(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getChecks().add(
                CheckState.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeLogSummariesAnUnmodifiableList() {
        // Given
        EnvironmentPluginState underTest = EnvironmentPluginState.builder()
                .logSummaries(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getLogSummaries().add(
                LogSummaryState.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
    
    @Test
    public void mergeShouldMergeChecksAndLogSummaries() {
        // Given
        EnvironmentPluginState underTest1 = EnvironmentPluginState.builder()
                .id("test-plugin-id-1")
                .checks(List.of(
                        createCheck(1),
                        createCheck(3)
                ))
                .logSummaries(List.of(
                        createLogSummary(1),
                        createLogSummary(3)
                ))
                .build();
        EnvironmentPluginState underTest2 = EnvironmentPluginState.builder()
                .id("test-plugin-id-2")
                .checks(List.of(
                        createCheck(2),
                        createCheck(4)
                ))
                .logSummaries(List.of(
                        createLogSummary(2),
                        createLogSummary(4)
                ))
                .build();
        
        // When
        EnvironmentPluginState returnValue = underTest1.merge(underTest2);
        
        // Then
        assertThat(returnValue).isEqualTo(
                EnvironmentPluginState.builder()
                        .id("test-plugin-id-1")
                        .checks(List.of(
                                createCheck(1),
                                createCheck(3),
                                createCheck(2),
                                createCheck(4)

                        ))
                        .logSummaries(List.of(
                                createLogSummary(1),
                                createLogSummary(3),
                                createLogSummary(2),
                                createLogSummary(4)
                        ))
                        .build()
        );
    }

    @Test
    public void addChecksWhenExistingCheckListIsEmptyShouldAddChecksToExistingList() {
        // Given
        List<CheckState> existingChecks = List.of();
        List<CheckState> newChecks = List.of(
                createCheck(1),
                createCheck(2)
        );
        EnvironmentPluginState underTest = EnvironmentPluginState.builder()
                .checks(existingChecks)
                .build();

        // When
        EnvironmentPluginState returnValue = underTest.addChecks(newChecks);

        // Then
        assertThat(returnValue.getChecks()).containsExactly(
                createCheck(1),
                createCheck(2)
        );
    }

    @Test
    public void addChecksWhenExistingCheckListIsNotEmptyShouldAddChecksToExistingList() {
        // Given
        List<CheckState> existingChecks = List.of(
                createCheck(3),
                createCheck(4)
        );
        List<CheckState> newChecks = List.of(
                createCheck(1),
                createCheck(2)
        );
        EnvironmentPluginState underTest = EnvironmentPluginState.builder()
                .checks(existingChecks)
                .build();

        // When
        EnvironmentPluginState returnValue = underTest.addChecks(newChecks);

        // Then
        assertThat(returnValue.getChecks()).containsExactly(
                createCheck(3),
                createCheck(4),
                createCheck(1),
                createCheck(2)
        );
    }

    @Test
    public void addLogSummariesWhenExistingLogSummaryListIsEmptyShouldAddLogSummariesToExistingList() {
        // Given
        List<LogSummaryState> existingLogSummaries = List.of();
        List<LogSummaryState> newLogSummaries = List.of(
                createLogSummary(1),
                createLogSummary(2)
        );
        EnvironmentPluginState underTest = EnvironmentPluginState.builder()
                .logSummaries(existingLogSummaries)
                .build();

        // When
        EnvironmentPluginState returnValue = underTest.addLogSummaries(newLogSummaries);

        // Then
        assertThat(returnValue.getLogSummaries()).containsExactly(
                createLogSummary(1),
                createLogSummary(2)
        );
    }

    @Test
    public void addLogSummariesWhenExistingLogSummaryListIsNotEmptyShouldAddLogSummariesToExistingList() {
        // Given
        List<LogSummaryState> existingLogSummaries = List.of(
                createLogSummary(3),
                createLogSummary(4)
        );
        List<LogSummaryState> newLogSummaries = List.of(
                createLogSummary(1),
                createLogSummary(2)
        );
        EnvironmentPluginState underTest = EnvironmentPluginState.builder()
                .logSummaries(existingLogSummaries)
                .build();

        // When
        EnvironmentPluginState returnValue = underTest.addLogSummaries(newLogSummaries);

        // Then
        assertThat(returnValue.getLogSummaries()).containsExactly(
                createLogSummary(3),
                createLogSummary(4),
                createLogSummary(1),
                createLogSummary(2)
        );
    }

    private CheckState createCheck(int checkNumber) {
        return CheckState.builder()
                .name("test-check-name-" + checkNumber)
                .build();
    }

    private LogSummaryState createLogSummary(int logSummaryNumber) {
        return LogSummaryState.builder()
                .name("test-log-summary-name-" + logSummaryNumber)
                .build();
    }
}
