package tech.kronicle.tracingprocessor.internal.models;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TimestampsForEdge {

    LocalDateTime startTimestamp;
    LocalDateTime endTimestamp;
}
