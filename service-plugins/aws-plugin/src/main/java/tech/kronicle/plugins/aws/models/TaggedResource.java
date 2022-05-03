package tech.kronicle.plugins.aws.models;

import lombok.Value;

@Value
public class TaggedResource {

    String resourceId;
    String environmentId;
}
