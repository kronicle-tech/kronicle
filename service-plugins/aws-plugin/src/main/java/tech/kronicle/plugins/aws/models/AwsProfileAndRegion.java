package tech.kronicle.plugins.aws.models;

import lombok.Value;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;

@Value
public class AwsProfileAndRegion {

    AwsProfileConfig profile;
    String region;
}
