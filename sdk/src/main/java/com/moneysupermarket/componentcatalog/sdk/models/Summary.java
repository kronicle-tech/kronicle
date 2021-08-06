package com.moneysupermarket.componentcatalog.sdk.models;

import com.moneysupermarket.componentcatalog.sdk.models.sonarqube.SummarySonarQubeMetric;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

import static com.moneysupermarket.componentcatalog.sdk.utils.ListUtils.createUnmodifiableList;

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
