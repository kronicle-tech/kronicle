package tech.kronicle.plugins.aws.cloudwatchlogs.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;
import tech.kronicle.plugins.aws.AwsPlugin;
import tech.kronicle.plugins.aws.cloudwatchlogs.client.CloudWatchLogsClientFacade;
import tech.kronicle.plugins.aws.cloudwatchlogs.client.CloudWatchLogsClientFacadeFactory;
import tech.kronicle.plugins.aws.cloudwatchlogs.constants.CloudWatchQueryStatuses;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResult;
import tech.kronicle.plugins.aws.cloudwatchlogs.models.CloudWatchLogsQueryResults;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.guice.CloudWatchLogsGetQueryResultsRetry;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.models.ComponentData;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentStateLogLevelCount;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNullElse;
import static tech.kronicle.plugins.aws.utils.ProfileUtils.processProfilesToMap;

public class CloudWatchLogsService {

    private static final Pattern QUERY_FIELD_PATTERN = Pattern.compile("^[_a-zA-Z]+$");
    public static final String DEFAULT_LOG_LEVEL_FIELD_NAME = "level";

    private final CloudWatchLogsClientFacadeFactory clientFacadeFactory;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final Retry retry;
    private final AwsConfig config;
    private Map<AwsProfileAndRegion, List<String>> logGroupNamesByProfileAndRegion;

    @Inject
    public CloudWatchLogsService(
            CloudWatchLogsClientFacadeFactory clientFacadeFactory,
            ObjectMapper objectMapper,
            Clock clock,
            @CloudWatchLogsGetQueryResultsRetry Retry retry,
            AwsConfig config
    ) {
        this.clientFacadeFactory = clientFacadeFactory;
        this.objectMapper = objectMapper;
        this.clock = clock;
        this.retry = retry;
        this.config = config;
    }

    public void refresh() {
        logGroupNamesByProfileAndRegion = processProfilesToMap(config.getProfiles(), this::getLogGroupNames);
    }

    private List<String> getLogGroupNames(AwsProfileAndRegion profileAndRegion) {
        return clientFacadeFactory.createCloudWatchLogsClientFacade(profileAndRegion)
                .getLogGroupNames();
    }

    @SneakyThrows
    public Map<AwsProfileAndRegion, List<ComponentStateLogLevelCount>> getLogLevelCounts(Component component) {
        ComponentData componentData = getComponentData(component);

        if (isNull(componentData)) {
            return Map.of();
        }

        List<Pattern> logGroupNamePatterns = getLogGroupNamePatterns(componentData);

        if (logGroupNamePatterns.isEmpty()) {
            return Map.of();
        }

        String logLevelFieldName = getLogLevelFieldName(componentData);

        return processProfilesToMap(
                config.getProfiles(),
                getLogLevelCounts(logGroupNamePatterns, logLevelFieldName)
        );
    }

    private Function<AwsProfileAndRegion, List<ComponentStateLogLevelCount>> getLogLevelCounts(
            List<Pattern> logGroupNamePatterns,
            String logLevelFieldName
    ) {
        return profileAndRegion -> {
            List<String> matchingLogGroupNames = logGroupNamesByProfileAndRegion.get(profileAndRegion).stream()
                    .filter(matchesAnyPattern(logGroupNamePatterns))
                    .collect(Collectors.toList());

            Instant now = clock.instant();
            CloudWatchLogsQueryResults finalResults = executeQuery(
                    profileAndRegion,
                    logLevelFieldName,
                    matchingLogGroupNames,
                    now
            );

            return finalResults.getResults().stream()
                    .map(this::mapResult)
                    .collect(Collectors.toList());
        };
    }

    @SneakyThrows
    private CloudWatchLogsQueryResults executeQuery(
            AwsProfileAndRegion profileAndRegion,
            String logLevelFieldName,
            List<String> matchingLogGroupNames,
            Instant now
    ) {
        CloudWatchLogsClientFacade clientFacade = clientFacadeFactory.createCloudWatchLogsClientFacade(
                profileAndRegion
        );
        String queryId = clientFacade.startQuery(
                now.minus(Duration.ofHours(1)).getEpochSecond(),
                now.getEpochSecond(),
                matchingLogGroupNames,
                "stats count(*) by " + logLevelFieldName + " as level"
        );

        return retry.executeCallable(() -> {
            CloudWatchLogsQueryResults results = clientFacade.getQueryResults(queryId);

            switch (results.getStatus()) {
                case CloudWatchQueryStatuses.SCHEDULED:
                    throw new RuntimeException("Query had not started yet");
                case CloudWatchQueryStatuses.RUNNING:
                    throw new RuntimeException("Query is still running");
                case CloudWatchQueryStatuses.COMPLETE:
                    return results;
                default:
                    throw new RuntimeException("Query has failed with status " + results.getStatus());
            }
        });
    }

    private ComponentData getComponentData(Component component) {
        return Optional.ofNullable(component.getPlugins())
                .map(plugins -> plugins.get(AwsPlugin.ID))
                .map(this::readComponentData)
                .orElse(null);
    }

    private List<Pattern> getLogGroupNamePatterns(ComponentData componentData) {
        return Optional.of(componentData)
                .map(ComponentData::getLogGroupNamePatterns)
                .map(logGroupNamePatterns -> logGroupNamePatterns.stream()
                        .map(Pattern::compile)
                        .collect(Collectors.toList())
                )
                .orElse(List.of());
    }

    private String getLogLevelFieldName(ComponentData componentData) {
        String logLevelFieldName = requireNonNullElse(componentData.getLogLevelFieldName(), DEFAULT_LOG_LEVEL_FIELD_NAME);
        if (!QUERY_FIELD_PATTERN.matcher(logLevelFieldName).matches()) {
            throw new RuntimeException("Invalid log level field name '" + logLevelFieldName + "'");
        }
        return logLevelFieldName;
    }

    private Predicate<String> matchesAnyPattern(List<Pattern> logGroupNamePatterns) {
        return logGroupName -> matchesAnyPattern(logGroupNamePatterns, logGroupName);
    }

    private boolean matchesAnyPattern(List<Pattern> patterns, String value) {
        return patterns.stream().anyMatch(pattern -> pattern.matcher(value).find());
    }

    @SneakyThrows
    private ComponentData readComponentData(String componentDataJson) {
        return objectMapper.readValue(componentDataJson, ComponentData.class);
    }

    private ComponentStateLogLevelCount mapResult(CloudWatchLogsQueryResult result) {
        return ComponentStateLogLevelCount.builder()
                .level(getResultFieldValue(result, "level"))
                .count(Long.parseLong(getResultFieldValue(result, "count(*)")))
                .build();
    }

    private String getResultFieldValue(CloudWatchLogsQueryResult result, String name) {
        return result.getFields().stream()
                .filter(field -> Objects.equals(field.getField(), name))
                .findFirst()
                .get()
                .getValue();
    }
}
