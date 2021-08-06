package com.moneysupermarket.componentcatalog.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class Import implements ObjectWithScannerId {

    @NotBlank
    String scannerId;
    @NotNull
    ImportType type;
    @NotBlank
    String name;
}
