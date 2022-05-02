package tech.kronicle.plugins.aws.cloudwatchlogs.models;

import lombok.Value;
import tech.kronicle.sdk.models.LogSummaryState;

@Value
public class LogSummaryStateAndContext {

    String environmentId;
    LogSummaryState logSummary;
}
