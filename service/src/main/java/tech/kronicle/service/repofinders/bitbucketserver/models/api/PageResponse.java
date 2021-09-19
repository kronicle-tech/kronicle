package tech.kronicle.service.repofinders.bitbucketserver.models.api;

import lombok.Value;

import java.util.List;

/**
 * This class only contains a subset of the fields returned by the Bitbucket Server API
 */
@Value
public class PageResponse<T> {

    Integer size;
    Integer limit;
    Boolean isLastPage;
    List<T> values;
    Integer start;
    Integer nextPageStart;
}
