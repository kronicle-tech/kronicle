package com.moneysupermarket.componentcatalog.sdk.models;

import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
public class GetScannersResponse {

    @NotNull
    List<@NotNull @Valid Scanner> scanners;
}
