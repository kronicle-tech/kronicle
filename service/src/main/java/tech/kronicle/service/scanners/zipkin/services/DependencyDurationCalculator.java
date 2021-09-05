package tech.kronicle.service.scanners.zipkin.services;

import tech.kronicle.sdk.models.SummaryComponentDependencyDuration;
import tech.kronicle.service.scanners.zipkin.models.ObjectWithDurations;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DependencyDurationCalculator {

    public <D extends ObjectWithDurations> SummaryComponentDependencyDuration calculateDependencyDuration(List<D> dependencies) {
        List<Long> durations = getSortedDurations(dependencies);
        if (durations.isEmpty()) {
            return null;
        }
        Percentile percentile = new Percentile()
                .withEstimationType(Percentile.EstimationType.R_1);
        percentile.setData(getDurationArray(durations));
        return new SummaryComponentDependencyDuration(
                durations.get(0),
                durations.get(durations.size() - 1),
                Math.round(percentile.evaluate(50)),
                Math.round(percentile.evaluate(90)),
                Math.round(percentile.evaluate(99)),
                Math.round(percentile.evaluate(99.9)));
    }

    private <D extends ObjectWithDurations> List<Long> getSortedDurations(List<D> dependencies) {
        return dependencies.stream()
                .map(D::getDurations)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }

    private double[] getDurationArray(List<Long> durations) {
        return durations.stream()
                .mapToDouble(Long::doubleValue)
                .toArray();
    }
}
