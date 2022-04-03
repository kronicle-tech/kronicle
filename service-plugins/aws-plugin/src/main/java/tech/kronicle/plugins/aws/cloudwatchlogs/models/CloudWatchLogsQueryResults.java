package tech.kronicle.plugins.aws.cloudwatchlogs.models;

import lombok.Value;
import tech.kronicle.plugins.aws.cloudwatchlogs.constants.CloudWatchQueryStatuses;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
public class CloudWatchLogsQueryResults {

    public static final CloudWatchLogsQueryResults EMPTY =
            new CloudWatchLogsQueryResults(CloudWatchQueryStatuses.COMPLETE, List.of());

    String status;
    List<CloudWatchLogsQueryResult> results;

    public CloudWatchLogsQueryResults(String status, List<CloudWatchLogsQueryResult> results) {
        this.status = status;
        this.results = createUnmodifiableList(results);
    }
}
