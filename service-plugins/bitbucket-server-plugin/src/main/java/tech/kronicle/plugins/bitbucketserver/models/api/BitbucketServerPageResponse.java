package tech.kronicle.plugins.bitbucketserver.models.api;

import lombok.Value;

import java.util.List;

/**
 * This class only contains a subset of the fields returned by the Bitbucket Server API
 */
@Value
public class BitbucketServerPageResponse<T> {

    Integer size;
    Integer limit;
    Boolean isLastPage;
    List<T> values;
    Integer start;
    Integer nextPageStart;
}
