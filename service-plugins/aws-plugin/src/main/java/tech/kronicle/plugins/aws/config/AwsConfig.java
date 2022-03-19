package tech.kronicle.plugins.aws.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@NonFinal
public class AwsConfig {

    List<AwsProfileConfig> profiles;
    Boolean detailedComponentDescriptions;

    public AwsConfig(List<AwsProfileConfig> profiles, Boolean detailedComponentDescriptions) {
        this.profiles = createUnmodifiableList(profiles);
        this.detailedComponentDescriptions = detailedComponentDescriptions;
    }
}
