package com.moneysupermarket.componentcatalog.service.scanners.zipkin.models;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TimestampsForDependency {

    LocalDateTime startTimestamp;
    LocalDateTime endTimestamp;
}
