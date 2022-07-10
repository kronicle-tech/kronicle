package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.pluginapi.finders.DiagramFinder;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Diagram;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.utils.StreamUtils.distinctByKey;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterDiagramFinder {

    private final FinderExtensionRegistry registry;
    private final ExtensionExecutor executor;

    public List<Diagram> findDiagrams(ComponentMetadata componentMetadata) {
        return registry.getDiagramFinders().stream()
                .map(finder -> executeFinder(finder, componentMetadata))
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }

    private List<Component> getComponents(
            List<ComponentsAndDiagrams> componentsAndDiagramsList,
            ComponentMetadata componentMetadata
    ) {
        return componentsAndDiagramsList.stream()
                .map(ComponentsAndDiagrams::getComponents)
                .flatMap(Collection::stream)
                .filter(distinctByKey(Component::getId))
                .filter(componentDoesNotAlreadyExist(componentMetadata))
                .map(component -> component.withDiscovered(true))
                .collect(Collectors.toList());
    }

    private List<Diagram> getDiagrams(List<ComponentsAndDiagrams> componentsAndDiagramsList) {
        return componentsAndDiagramsList.stream()
                .map(ComponentsAndDiagrams::getDiagrams)
                .flatMap(Collection::stream)
                .map(diagram -> diagram.withDiscovered(true))
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

    private List<Diagram> executeFinder(DiagramFinder finder, ComponentMetadata componentMetadata) {
        Output<List<Diagram>, Void> output = executor.executeFinder(finder, null, componentMetadata);
        if (output.success()) {
            log.info(
                    "Component finder {} found {} diagrams",
                    finder.id(),
                    output.getOutput().size()
            );
        }
        return output.getOutputOrElse(List.of());
    }
}
