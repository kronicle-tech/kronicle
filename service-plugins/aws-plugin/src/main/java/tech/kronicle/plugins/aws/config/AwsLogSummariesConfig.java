package tech.kronicle.plugins.aws.config;

import lombok.Value;

@Value
public class AwsLogSummariesConfig {

    Boolean oneHourSummaries;
    Boolean twentyFourHourSummaries;
}
