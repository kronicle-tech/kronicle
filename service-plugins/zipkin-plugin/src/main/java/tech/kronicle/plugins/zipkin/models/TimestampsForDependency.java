package tech.kronicle.plugins.zipkin.models;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TimestampsForDependency {

    LocalDateTime startTimestamp;
    LocalDateTime endTimestamp;
}
