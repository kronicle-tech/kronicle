package com.moneysupermarket.componentcatalog.service.scanners.zipkin.models;

import com.moneysupermarket.componentcatalog.sdk.models.ObjectWithSourceIndexAndTargetIndex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

import static java.util.Objects.nonNull;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
@Jacksonized
public class CollatorComponentDependency implements ObjectWithSourceIndexAndTargetIndex, ObjectWithTimestamps, ObjectWithDurations {

    Integer sourceIndex;
    Integer targetIndex;
    List<Integer> relatedIndexes;
    Long timestamp;
    Long duration;

    @Override
    public List<Long> getTimestamps() {
        return nonNull(timestamp) ? List.of(timestamp) : List.of();
    }

    @Override
    public List<Long> getDurations() {
        return nonNull(duration) ? List.of(duration) : List.of();
    }
}
