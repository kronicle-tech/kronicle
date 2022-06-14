package tech.kronicle.tracingprocessor.internal.services;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import tech.kronicle.sdk.models.GraphEdgeDuration;

import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

public class EdgeDurationCalculator {

    public GraphEdgeDuration calculateEdgeDuration(List<Long> durations) {
        List<Long> sortedDurations = getSortedDurations(durations);
        if (sortedDurations.isEmpty()) {
            return null;
        }
        Percentile percentile = new Percentile()
                .withEstimationType(Percentile.EstimationType.R_1);
        percentile.setData(getDurationArray(sortedDurations));
        return new GraphEdgeDuration(
                sortedDurations.get(0),
                sortedDurations.get(sortedDurations.size() - 1),
                Math.round(percentile.evaluate(50)),
                Math.round(percentile.evaluate(90)),
                Math.round(percentile.evaluate(99)),
                Math.round(percentile.evaluate(99.9))
        );
    }

    private List<Long> getSortedDurations(List<Long> durations) {
        return durations.stream()
                .sorted()
                .collect(toUnmodifiableList());
    }

    private double[] getDurationArray(List<Long> durations) {
        return durations.stream()
                .mapToDouble(Long::doubleValue)
                .toArray();
    }
}
