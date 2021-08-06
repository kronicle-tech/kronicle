package com.moneysupermarket.componentcatalog.sdk.models;

import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Value
public class GetSummaryResponse {

    @NotNull
    @Valid
    Summary summary;
}
