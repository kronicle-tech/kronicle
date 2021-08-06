package com.moneysupermarket.componentcatalog.sdk.models;

import lombok.Value;

import javax.validation.Valid;

@Value
public class GetComponentResponse {

    @Valid
    Component component;
}
