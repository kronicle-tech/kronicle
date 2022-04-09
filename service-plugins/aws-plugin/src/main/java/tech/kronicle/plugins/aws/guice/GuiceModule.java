package tech.kronicle.plugins.aws.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import tech.kronicle.plugins.aws.cloudwatchlogs.client.CloudWatchLogsClientFacade;
import tech.kronicle.plugins.aws.cloudwatchlogs.client.CloudWatchLogsClientFacadeImpl;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.client.ResourceGroupsTaggingApiClientFacade;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.client.ResourceGroupsTaggingApiClientFacadeImpl;

import java.time.Clock;
import java.time.Duration;

import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CloudWatchLogsClientFacade.class).to(CloudWatchLogsClientFacadeImpl.class);
        bind(ResourceGroupsTaggingApiClientFacade.class).to(ResourceGroupsTaggingApiClientFacadeImpl.class);
    }

    @Provides
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Provides
    public ObjectMapper objectMapper() {
        return createJsonMapper();
    }

    @Provides
    @CloudWatchLogsGetQueryResultsRetry
    public Retry retryRegistry() {
        return Retry.of(
                "cloudWatchLogsGetQueryResults",
                RetryConfig.custom()
                        .maxAttempts(10)
                        .intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofSeconds(1), 2))
                        .build()
        );
    }
}
