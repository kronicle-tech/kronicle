package tech.kronicle.plugins.aws.cloudwatchlogs.models;

import lombok.Value;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
public class CloudWatchLogsQueryResults {

    String status;
    List<CloudWatchLogsQueryResult> results;

    public CloudWatchLogsQueryResults(String status, List<CloudWatchLogsQueryResult> results) {
        this.status = status;
        this.results = createUnmodifiableList(results);
    }
}
