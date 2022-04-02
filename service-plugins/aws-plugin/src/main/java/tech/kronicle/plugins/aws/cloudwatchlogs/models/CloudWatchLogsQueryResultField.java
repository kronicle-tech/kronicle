package tech.kronicle.plugins.aws.cloudwatchlogs.models;

import lombok.Value;

@Value
public class CloudWatchLogsQueryResultField {

    String field;
    String value;
}
