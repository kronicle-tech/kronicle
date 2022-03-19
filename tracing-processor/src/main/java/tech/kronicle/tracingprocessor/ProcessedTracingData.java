package tech.kronicle.tracingprocessor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import tech.kronicle.sdk.models.SummaryCallGraph;
import tech.kronicle.sdk.models.SummaryComponentDependencies;
import tech.kronicle.sdk.models.SummarySubComponentDependencies;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@With
public class ProcessedTracingData {

    SummaryComponentDependencies componentDependencies;
    SummarySubComponentDependencies subComponentDependencies;
    List<SummaryCallGraph> callGraphs;
}
