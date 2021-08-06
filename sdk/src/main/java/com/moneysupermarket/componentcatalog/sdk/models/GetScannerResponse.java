package com.moneysupermarket.componentcatalog.sdk.models;

import lombok.Value;

import javax.validation.Valid;

@Value
public class GetScannerResponse {

    @Valid
    Scanner scanner;
}
