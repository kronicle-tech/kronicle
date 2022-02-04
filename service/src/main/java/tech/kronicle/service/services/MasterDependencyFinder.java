package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterDependencyFinder {

    private final FinderRegistry finderRegistry;

    public List<Dependency> getDependencies(ComponentMetadata componentMetadata) {
        return finderRegistry.getDependencyFinders().stream()
                .map(finder -> finder.find(componentMetadata))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
