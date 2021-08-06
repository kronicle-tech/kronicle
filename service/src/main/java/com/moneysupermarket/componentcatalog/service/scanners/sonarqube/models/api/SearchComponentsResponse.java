package com.moneysupermarket.componentcatalog.service.scanners.sonarqube.models.api;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

import static com.moneysupermarket.componentcatalog.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class SearchComponentsResponse {

    Paging paging;
    List<Component> components;

    public SearchComponentsResponse(Paging paging, List<Component> components) {
        this.paging = paging;
        this.components = createUnmodifiableList(components);
    }
}
