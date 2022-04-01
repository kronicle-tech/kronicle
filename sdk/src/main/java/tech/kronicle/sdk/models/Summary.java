package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.models.sonarqube.SummarySonarQubeMetric;
import tech.kronicle.sdk.utils.ListUtils;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Summary {

    public static final Summary EMPTY = Summary.builder().build();

    List<SummaryMissingComponent> missingComponents;
    SummaryComponentDependencies componentDependencies;
    SummarySubComponentDependencies subComponentDependencies;
    List<SummaryCallGraph> callGraphs;
    List<SummarySonarQubeMetric> sonarQubeMetrics;

    public Summary(List<SummaryMissingComponent> missingComponents, SummaryComponentDependencies componentDependencies,
            SummarySubComponentDependencies subComponentDependencies, List<SummaryCallGraph> callGraphs, List<SummarySonarQubeMetric> sonarQubeMetrics) {
        this.missingComponents = createUnmodifiableList(missingComponents);
        this.componentDependencies = componentDependencies;
        this.subComponentDependencies = subComponentDependencies;
        this.callGraphs = createUnmodifiableList(callGraphs);
        this.sonarQubeMetrics = createUnmodifiableList(sonarQubeMetrics);
    }
}
