package tech.kronicle.plugins.aws.cloudwatchlogs.client;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CloudWatchLogsClientFacadeFactory {

    private final CloudWatchLogsClientFactory clientFactory;
    
    public CloudWatchLogsClientFacade createCloudWatchLogsClientFacade(AwsProfileAndRegion profileAndRegion) {
        return new CloudWatchLogsClientFacadeImpl(clientFactory.createCloudWatchLogsClient(profileAndRegion));
    }
}
