package tech.kronicle.plugins.aws.cloudwatchlogs.models;

import lombok.Value;

import java.util.List;

@Value
public class CloudWatchLogsQueryResult {

    List<CloudWatchLogsQueryResultField> fields;
}
