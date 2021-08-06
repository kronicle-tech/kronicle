package com.moneysupermarket.componentcatalog.service.scanners.openapi.models;

import com.moneysupermarket.componentcatalog.sdk.models.ScannerError;
import com.moneysupermarket.componentcatalog.sdk.models.openapi.OpenApiSpec;
import lombok.Value;
import lombok.With;

import java.util.List;

@Value
@With
public class SpecAndErrors {

    OpenApiSpec spec;
    List<ScannerError> errors;
}
