package com.moneysupermarket.componentcatalog.service.scanners.zipkin.services;

import com.moneysupermarket.componentcatalog.sdk.models.SummarySubComponentDependencies;
import com.moneysupermarket.componentcatalog.sdk.models.SummarySubComponentDependencyNode;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.models.api.Span;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
