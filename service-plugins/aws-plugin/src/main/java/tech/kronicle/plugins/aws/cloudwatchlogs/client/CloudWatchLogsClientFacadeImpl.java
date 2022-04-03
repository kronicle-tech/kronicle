package tech.kronicle.plugins.aws.cloudwatchlogs.client;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.GetQueryResultsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.ResultField;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResult;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResultField;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResults;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CloudWatchLogsClientFacadeImpl implements CloudWatchLogsClientFacade {

    private final CloudWatchLogsClient client;

    @Override
    public void close() {
        client.close();
    }

    public String startQuery(long startTime, long endTime, List<String> logGroupNames, String query) {
        return client
                .startQuery(
                    builder -> builder.startTime(startTime)
                            .endTime(endTime)
                            .logGroupNames(logGroupNames)
                            .queryString(query)
                )
                .queryId();
    }

    public CloudWatchLogsQueryResults getQueryResults(String queryId) {
        return mapQueryResults(client
                .getQueryResults(
                        builder -> builder.queryId(queryId)
                ));
    }

    private CloudWatchLogsQueryResults mapQueryResults(GetQueryResultsResponse queryResults) {
        return new CloudWatchLogsQueryResults(
                queryResults.statusAsString(),
                mapQueryResults(queryResults.results())
        );
    }

    private List<CloudWatchLogsQueryResult> mapQueryResults(List<List<ResultField>> results) {
        return results.stream()
                .map(this::mapQueryResult)
                .collect(Collectors.toList());
    }

    private CloudWatchLogsQueryResult mapQueryResult(List<ResultField> result) {
        return new CloudWatchLogsQueryResult(
                result.stream()
                        .map(this::mapQueryResultField)
                        .collect(Collectors.toList())
        );
    }

    private CloudWatchLogsQueryResultField mapQueryResultField(ResultField resultField) {
        return new CloudWatchLogsQueryResultField(
                resultField.field(),
                resultField.value()
        );
    }
}
