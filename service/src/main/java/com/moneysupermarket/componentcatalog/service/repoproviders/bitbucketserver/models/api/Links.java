package com.moneysupermarket.componentcatalog.service.repoproviders.bitbucketserver.models.api;

import lombok.Value;

import java.util.List;

/**
 * This class only contains a subset of the fields returned by the Bitbucket Server API
 */
@Value
public class Links {

    private List<Link> clone;
}
