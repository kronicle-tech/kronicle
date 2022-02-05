package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.service.finders.DependencyFinder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterDependencyFinder {

    private final FinderRegistry finderRegistry;

    public List<Dependency> getDependencies(ComponentMetadata componentMetadata) {
        return finderRegistry.getDependencyFinders().stream()
                .map(finder -> executeFinder(finder, componentMetadata))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Dependency> executeFinder(DependencyFinder finder, ComponentMetadata componentMetadata) {
        List<Dependency> dependencies;
        try {
            dependencies = finder.find(componentMetadata);
        } catch (Exception e) {
            log.error("Failed to execute dependency finder {}", finder.id(), e);
            return List.of();
        }

        log.info("Dependency finder {} found {} dependencies", finder.id(), dependencies.size());
        return dependencies;
    }
}
