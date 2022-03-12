package tech.kronicle.plugins.aws.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@NonFinal
public class AwsConfig {

    List<AwsProfileConfig> profiles;

    public AwsConfig(List<AwsProfileConfig> profiles) {
        this.profiles = createUnmodifiableList(profiles);
    }
}
