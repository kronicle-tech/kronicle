package com.moneysupermarket.componentcatalog.sdk.models.sonarqube;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true)
public class SummarySonarQubeMetric {
    
    String id;
    String key;
    String type;
    String name;
    String description;
    String domain;
    Integer direction;
    Boolean qualitative;
    Boolean hidden;
    Boolean custom;
}
