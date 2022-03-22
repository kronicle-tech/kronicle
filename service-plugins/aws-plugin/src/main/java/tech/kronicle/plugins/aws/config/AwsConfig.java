package tech.kronicle.plugins.aws.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@NonFinal
public class AwsConfig {

    List<AwsProfileConfig> profiles;
    @NotNull
    Boolean detailedComponentDescriptions;
    @NotEmpty
    String teamTagName;

    public AwsConfig(List<AwsProfileConfig> profiles, Boolean detailedComponentDescriptions, String teamTagName) {
        this.profiles = createUnmodifiableList(profiles);
        this.detailedComponentDescriptions = detailedComponentDescriptions;
        this.teamTagName = teamTagName;
    }
}
