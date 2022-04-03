package tech.kronicle.plugins.aws.cloudwatchlogs.client;

import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResults;

import java.util.List;

public interface CloudWatchLogsClientFacade extends AutoCloseable {

    String startQuery(long startTime, long endTime, List<String> logGroupNames, String queryString);

    CloudWatchLogsQueryResults getQueryResults(String queryId);

    @Override
    void close();
}
