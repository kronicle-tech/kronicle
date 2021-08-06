package com.moneysupermarket.componentcatalog.sdk.models.gradle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class Gradle {

    Boolean used;
}
