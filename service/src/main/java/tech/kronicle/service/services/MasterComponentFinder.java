package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.finders.ComponentFinder;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterComponentFinder {

    private final FinderExtensionRegistry finderRegistry;

    public List<Component> findComponents(ComponentMetadata componentMetadata) {
        return finderRegistry.getComponentFinders().stream()
                .map(finder -> executeFinder(finder, componentMetadata))
                .flatMap(Collection::stream)
                .filter(distinctByKey(Component::getId))
                .filter(componentDoesNotAlreadyExist(componentMetadata))
                .map(component -> component.withDiscovered(true))
                .collect(Collectors.toList());
    }

    private static Predicate<Component> componentDoesNotAlreadyExist(ComponentMetadata componentMetadata) {
        Set<String> existingComponentIdSet = getExistingComponentIdSet(componentMetadata);
        return component -> !existingComponentIdSet.contains(component.getId());
    }

    private static Set<String> getExistingComponentIdSet(ComponentMetadata componentMetadata) {
        return Set.copyOf(
                componentMetadata.getComponents().stream()
                        .map(Component::getId)
                        .collect(Collectors.toList())
        );
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new HashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private List<Component> executeFinder(ComponentFinder finder, ComponentMetadata componentMetadata) {
        List<Component> components;
        try {
            components = finder.find(componentMetadata);
        } catch (Exception e) {
            log.error("Failed to execute component finder {}", finder.id(), e);
            return List.of();
        }

        log.info("Component finder {} found {} components", finder.id(), components.size());
        return components;
    }

}
