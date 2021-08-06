package com.moneysupermarket.componentcatalog.sdk.models;

import lombok.Value;

import javax.validation.Valid;

@Value
public class GetTestResponse {

    @Valid
    Test test;
}
