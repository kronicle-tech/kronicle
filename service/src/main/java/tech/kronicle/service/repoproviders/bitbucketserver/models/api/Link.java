package tech.kronicle.service.repoproviders.bitbucketserver.models.api;

import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * This class only contains a subset of the fields returned by the Bitbucket Server API
 */
@Value
public class Link {

    String href;
    String name;
}
