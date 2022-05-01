package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.finders.ComponentFinder;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static tech.kronicle.utils.StreamUtils.distinctByKey;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterComponentFinder {

    private final FinderExtensionRegistry registry;
    private final ExtensionExecutor executor;

    public List<Component> findComponents(ComponentMetadata componentMetadata) {
        return registry.getComponentFinders().stream()
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

    private List<Component> executeFinder(ComponentFinder finder, ComponentMetadata componentMetadata) {
        Output<List<Component>, Void> components = executor.executeFinder(finder, componentMetadata);
        if (components.success()) {
            log.info("Component finder {} found {} components", finder.id(), components.getOutput().size());
        }
        return components.getOutputOrElse(List.of());
    }

}
