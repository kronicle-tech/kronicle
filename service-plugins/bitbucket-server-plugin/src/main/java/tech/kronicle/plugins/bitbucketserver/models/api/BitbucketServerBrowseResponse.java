package tech.kronicle.plugins.bitbucketserver.models.api;

import lombok.Value;

/**
 * This class only contains a subset of the fields returned by the Bitbucket Server API
 */
@Value
public class BitbucketServerBrowseResponse {

    String type;
}
