package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class SummaryComponentDependencyDuration {

    Long min;
    /**
     * Min percentile is also the 100th percentile.
     */
    Long max;
    /**
     * The 50th percentile is also the median.
     */
    Long p50;
    Long p90;
    Long p99;
    Long p99Point9;
}
