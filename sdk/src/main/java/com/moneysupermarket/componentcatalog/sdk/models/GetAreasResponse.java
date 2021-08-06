package com.moneysupermarket.componentcatalog.sdk.models;

import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
public class GetAreasResponse {

    @NotNull
    List<@Valid Area> areas;
}
