package tech.kronicle.service.repofinders.bitbucketserver.models.api;

import lombok.Value;

/**
 * This class only contains a subset of the fields returned by the Bitbucket Server API
 */
@Value
public class Link {

    String href;
    String name;
}
