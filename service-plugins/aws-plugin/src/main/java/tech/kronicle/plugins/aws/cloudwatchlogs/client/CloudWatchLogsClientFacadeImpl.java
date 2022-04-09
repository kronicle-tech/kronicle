package tech.kronicle.plugins.aws.cloudwatchlogs.client;

import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.GetQueryResultsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.ResultField;
import tech.kronicle.plugins.aws.client.BaseClientFacade;
import tech.kronicle.plugins.aws.client.ClientFactory;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResult;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResultField;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResults;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class CloudWatchLogsClientFacadeImpl extends BaseClientFacade<CloudWatchLogsClient>
        implements CloudWatchLogsClientFacade {

    @Inject
    public CloudWatchLogsClientFacadeImpl(ClientFactory<CloudWatchLogsClient> clientFactory) {
        super(clientFactory);
    }

    public String startQuery(
            AwsProfileAndRegion profileAndRegion,
            long startTime,
            long endTime,
            List<String> logGroupNames,
            String query
    ) {
        return getClient(profileAndRegion)
                .startQuery(
                    builder -> builder.startTime(startTime)
                            .endTime(endTime)
                            .logGroupNames(logGroupNames)
                            .queryString(query)
                )
                .queryId();
    }

    public CloudWatchLogsQueryResults getQueryResults(
            AwsProfileAndRegion profileAndRegion,
            String queryId
    ) {
        return mapQueryResults(getClient(profileAndRegion)
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
