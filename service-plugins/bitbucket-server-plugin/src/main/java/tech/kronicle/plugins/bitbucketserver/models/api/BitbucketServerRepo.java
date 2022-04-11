package tech.kronicle.plugins.bitbucketserver.models.api;

import lombok.Value;

/**
 * This class only contains a subset of the fields returned by the Bitbucket Server API
 */
@Value
public class BitbucketServerRepo {

    String slug;
    String name;
    String scmId;
    String state;
    BitbucketServerProject project;
    BitbucketServerLinks links;
}
