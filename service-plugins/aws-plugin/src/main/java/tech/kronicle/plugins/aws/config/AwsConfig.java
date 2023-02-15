package tech.kronicle.plugins.aws.config;

import lombok.Value;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
public class AwsConfig {

    List<@Valid AwsProfileConfig> profiles;
    @NotNull
    Boolean detailedComponentDescriptions;
    @NotNull
    Boolean copyResourceTagsToComponents;
    @NotNull
    Boolean createDependenciesForResources;
    @NotNull
    Boolean loadXrayTraceData;
    @NotNull
    @Valid
    AwsTagKeysConfig tagKeys;
    @NotNull
    @Valid
    AwsLogFieldsConfig logFields;
    @NotNull
    @Valid
    AwsLogSummariesConfig logSummaries;

    public AwsConfig(
            List<AwsProfileConfig> profiles,
            Boolean detailedComponentDescriptions,
            Boolean copyResourceTagsToComponents,
            Boolean createDependenciesForResources,
            Boolean loadXrayTraceData,
            AwsTagKeysConfig tagKeys,
            AwsLogFieldsConfig logFields,
            AwsLogSummariesConfig logSummaries
    ) {
        this.profiles = createUnmodifiableList(profiles);
        this.detailedComponentDescriptions = detailedComponentDescriptions;
        this.copyResourceTagsToComponents = copyResourceTagsToComponents;
        this.createDependenciesForResources = createDependenciesForResources;
        this.loadXrayTraceData = loadXrayTraceData;
        this.tagKeys = tagKeys;
        this.logFields = logFields;
        this.logSummaries = logSummaries;
    }
}
