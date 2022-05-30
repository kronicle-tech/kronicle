package tech.kronicle.sdk.models.testutils;

import tech.kronicle.sdk.models.LogLevelSummary;

public class LogLevelSummaryUtils {

    public static LogLevelSummary createLogLevelSummary(int logSummaryNumber, int logLevelSummaryNumber) {
        return LogLevelSummary.builder()
                .level("test-log-level-" + logSummaryNumber + "-" + logLevelSummaryNumber)
                .build();
    }

    private LogLevelSummaryUtils() {
    }
}
