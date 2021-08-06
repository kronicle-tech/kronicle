package com.moneysupermarket.componentcatalog.service.scanners.zipkin.models.api;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Annotation {

    Long timestamp;
    String value;
}
