package tech.kronicle.sdk.models.testutils;

import tech.kronicle.sdk.models.LogSummary;

import java.util.List;

import static tech.kronicle.sdk.models.testutils.LogLevelSummaryUtils.createLogLevelSummary;

public final class LogSummaryUtils {

    public static LogSummary createLogSummary(int logSummaryNumber) {
        return LogSummary.builder()
                .name("test-log-summary-" + logSummaryNumber)
                .levels(List.of(
                        createLogLevelSummary(logSummaryNumber, 1),
                        createLogLevelSummary(logSummaryNumber, 2)
                ))
                .build();
    }

    private LogSummaryUtils() {
    }
}
