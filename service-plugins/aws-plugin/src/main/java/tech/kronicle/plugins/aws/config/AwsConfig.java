package tech.kronicle.plugins.aws.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@NonFinal
public class AwsConfig {

    List<@Valid AwsProfileConfig> profiles;
    @NotNull
    Boolean detailedComponentDescriptions;
    @NotNull
    Boolean createDependenciesForResources;
    @NotNull
    @Valid
    AwsTagKeysConfig tagKeys;
    @NotNull
    @Valid
    AwsLogFieldsConfig logFields;

    public AwsConfig(
            List<AwsProfileConfig> profiles,
            Boolean detailedComponentDescriptions,
            Boolean createDependenciesForResources, AwsTagKeysConfig tagKeys,
            AwsLogFieldsConfig logFields
    ) {
        this.profiles = createUnmodifiableList(profiles);
        this.detailedComponentDescriptions = detailedComponentDescriptions;
        this.createDependenciesForResources = createDependenciesForResources;
        this.tagKeys = tagKeys;
        this.logFields = logFields;
    }
}
