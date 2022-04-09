package tech.kronicle.plugins.aws.cloudwatchlogs.client;

import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResults;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;

import java.util.List;

public interface CloudWatchLogsClientFacade extends AutoCloseable {

    String startQuery(
            AwsProfileAndRegion profileAndRegion,
            long startTime,
            long endTime,
            List<String> logGroupNames,
            String query
    );

    CloudWatchLogsQueryResults getQueryResults(
            AwsProfileAndRegion profileAndRegion,
            String queryId
    );

    @Override
    void close();
}
