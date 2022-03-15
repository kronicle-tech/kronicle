package tech.kronicle.plugins.aws.utils;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class AnalysedArn {

    String arn;
    String partition;
    String service;
    String region;
    String accountId;
    String resourceId;
    String derivedResourceType;
}
