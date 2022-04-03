package tech.kronicle.plugins.aws.cloudwatchlogs.models;

import lombok.Value;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
public class CloudWatchLogsQueryResult {

    List<CloudWatchLogsQueryResultField> fields;

    public CloudWatchLogsQueryResult(List<CloudWatchLogsQueryResultField> fields) {
        this.fields = createUnmodifiableList(fields);
    }
}
