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
