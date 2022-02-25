package tech.kronicle.plugins.zipkin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.plugins.zipkin.models.api.Span;
import tech.kronicle.sdk.models.SummarySubComponentDependencies;
import tech.kronicle.sdk.models.SummarySubComponentDependencyNode;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubComponentDependencyCollator {

    private final GenericDependencyCollator genericDependencyCollator;
    private final Comparator<SummarySubComponentDependencyNode> subComponentNodeComparator;
    private final DependencyHelper dependencyHelper;

    public SummarySubComponentDependencies collateDependencies(List<List<Span>> traces) {
        return dependencyHelper.createSubComponentDependencies(genericDependencyCollator.createDependencies(traces,
                dependencyHelper::createSubComponentDependencyNode, subComponentNodeComparator, dependencyHelper::mergeDuplicateDependencies));
    }
}
