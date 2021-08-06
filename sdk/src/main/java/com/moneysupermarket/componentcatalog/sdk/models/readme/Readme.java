package com.moneysupermarket.componentcatalog.sdk.models.readme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class Readme {

    String fileName;
    String content;
}
