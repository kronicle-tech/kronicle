package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.common.StringEscapeUtils;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.Scanner;
import tech.kronicle.pluginapi.scanners.models.Codebase;
import tech.kronicle.pluginapi.scanners.models.ComponentAndCodebase;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.*;
import tech.kronicle.service.exceptions.ValidationException;
import tech.kronicle.tracingprocessor.GraphProcessor;
import tech.kronicle.tracingprocessor.internal.services.ComponentAliasResolver;
import tech.kronicle.utils.MapCollectors;
import tech.kronicle.utils.ObjectReference;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.sdk.utils.ListUtils.unmodifiableUnionOfLists;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScanEngine {

    private final MasterComponentFinder masterComponentFinder;
    private final MasterTracingDataFinder masterTracingDataFinder;
    private final MasterDiagramFinder masterDiagramFinder;
    private final GraphProcessor graphProcessor;
    private final ComponentAliasMapCreator componentAliasMapCreator;
    private final ComponentAliasResolver componentAliasResolver;
    private final ScannerExtensionRegistry scannerRegistry;
    private final ExtensionExecutor executor;
    private final ValidatorService validatorService;
    private final ThrowableToScannerErrorMapper throwableToScannerErrorMapper;

    public void scan(
            ComponentMetadata componentMetadata,
            ConcurrentHashMap<String, Component> componentMap,
            ConcurrentHashMap<String, Diagram> diagramMap,
            Consumer<Summary> summaryConsumer
    ) {
        Consumer<UnaryOperator<Summary>> summaryTransformerConsumer = createSummaryTransformerConsumer(summaryConsumer);

        componentMetadata = findAndProcessExtraComponents(componentMetadata, componentMap, diagramMap);

        executeComponentScanners(componentMap, summaryTransformerConsumer, componentMetadata);

        Map<Codebase, List<String>> codebaseAndComponentIdsMap = executeRepoScanner(componentMap, summaryTransformerConsumer, componentMetadata);

        executeCodebaseScanners(componentMap, summaryTransformerConsumer, componentMetadata, codebaseAndComponentIdsMap);

        executeComponentAndCodebaseScanners(componentMap, summaryTransformerConsumer, componentMetadata, codebaseAndComponentIdsMap);

        componentMetadata = findAndProcessExtraDiagrams(componentMetadata, diagramMap);

        executeLateComponentScanners(componentMap, summaryTransformerConsumer, componentMetadata);
    }

    private Consumer<UnaryOperator<Summary>> createSummaryTransformerConsumer(Consumer<Summary> summaryConsumer) {
        ObjectReference<Summary> summary = new ObjectReference<>(Summary.EMPTY);
        return summaryTransformer -> {
            Summary transformedSummary = summaryTransformer.apply(summary.get());
            summaryConsumer.accept(transformedSummary);
            summary.set(transformedSummary);
        };
    }

    private ComponentMetadata findAndProcessExtraComponents(
            ComponentMetadata componentMetadata, 
            ConcurrentHashMap<String, Component> componentMap,
            ConcurrentHashMap<String, Diagram> diagramMap
    ) {
        ComponentsAndDiagrams extraComponentsAndDiagrams = masterComponentFinder.findComponentsAndDiagrams(componentMetadata);
        return addExtraComponentsAndDiagrams(componentMetadata, componentMap, diagramMap, extraComponentsAndDiagrams);
    }

    private ComponentMetadata addExtraComponentsAndDiagrams(
            ComponentMetadata componentMetadata, 
            ConcurrentHashMap<String, Component> componentMap,
            ConcurrentHashMap<String, Diagram> diagramMap,
            ComponentsAndDiagrams extraComponentsAndDiagrams
    ) {
        List<Component> components = addExtraComponents(componentMetadata, componentMap, extraComponentsAndDiagrams);
        List<Diagram> diagrams = addExtraDiagrams(componentMetadata, diagramMap, extraComponentsAndDiagrams);
        return componentMetadata.withComponents(components)
                .withDiagrams(diagrams);
    }

    private List<Component> addExtraComponents(
            ComponentMetadata componentMetadata,
            ConcurrentHashMap<String, Component> componentMap,
            ComponentsAndDiagrams extraComponentsAndDiagrams
    ) {
        List<Component> components = new ArrayList<>(componentMetadata.getComponents());
        components.addAll(extraComponentsAndDiagrams.getComponents());
        extraComponentsAndDiagrams.getComponents().forEach(component -> componentMap.put(component.getId(), component));
        return components;
    }

    private List<Diagram> addExtraDiagrams(
            ComponentMetadata componentMetadata,
            ConcurrentHashMap<String, Diagram> diagramMap,
            ComponentsAndDiagrams extraDiagramsAndDiagrams
    ) {
        List<Diagram> diagrams = new ArrayList<>(componentMetadata.getDiagrams());
        diagrams.addAll(extraDiagramsAndDiagrams.getDiagrams());
        extraDiagramsAndDiagrams.getDiagrams().forEach(diagram -> diagramMap.put(diagram.getId(), diagram));
        return diagrams;
    }

    private ComponentMetadata findAndProcessExtraDiagrams(ComponentMetadata componentMetadata, ConcurrentHashMap<String, Diagram> diagramMap) {
        List<TracingData> tracingData = masterTracingDataFinder.findTracingData(componentMetadata);
        List<TracingData> updatedTracingData = componentAliasResolver.tracingDataList(
                tracingData,
                componentAliasMapCreator.createComponentAliasMap(componentMetadata)
        );
        List<Diagram> tracingDiagrams = updatedTracingData.stream()
                .map(graphProcessor::processTracingData)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
        List<Diagram> foundDiagrams = masterDiagramFinder.findDiagrams(componentMetadata);
        return updateDiagrams(
                componentMetadata,
                diagramMap,
                unmodifiableUnionOfLists(List.of(tracingDiagrams, foundDiagrams))
        );
    }

    private ComponentMetadata updateDiagrams(
            ComponentMetadata componentMetadata,
            ConcurrentHashMap<String, Diagram> diagramMap,
            List<Diagram> extraDiagrams
    ) {
        List<Diagram> diagrams = Stream.of(componentMetadata.getDiagrams(), extraDiagrams)
                .flatMap(Collection::stream)
                .map(graphProcessor::processDiagram)
                .collect(toUnmodifiableList());
        diagrams.forEach(diagram -> diagramMap.put(diagram.getId(), diagram));
        return componentMetadata.withDiagrams(diagrams);
    }

    private void executeComponentScanners(ConcurrentHashMap<String, Component> componentMap, Consumer<UnaryOperator<Summary>> summaryTransformerConsumer, ComponentMetadata updatedComponentMetadata) {
        scannerRegistry.getComponentScanners().forEach(scanner -> executeScanner(
                updatedComponentMetadata,
                getFreshComponentAndComponentIdMap(componentMap),
                componentMap,
                scanner,
                summaryTransformerConsumer));
    }

    private Map<Codebase, List<String>> executeRepoScanner(ConcurrentHashMap<String, Component> componentMap, Consumer<UnaryOperator<Summary>> summaryTransformerConsumer, ComponentMetadata updatedComponentMetadata) {
        return executeScanner(
                updatedComponentMetadata,
                getFreshRepoReferenceAndComponentIdsMap(componentMap),
                componentMap,
                scannerRegistry.getRepoScanner(),
                summaryTransformerConsumer);
    }

    private void executeCodebaseScanners(ConcurrentHashMap<String, Component> componentMap, Consumer<UnaryOperator<Summary>> summaryTransformerConsumer, ComponentMetadata updatedComponentMetadata, Map<Codebase, List<String>> codebaseAndComponentIdsMap) {
        scannerRegistry.getCodebaseScanners().forEach(scanner -> executeScanner(
                updatedComponentMetadata,
                codebaseAndComponentIdsMap,
                componentMap,
                scanner,
                summaryTransformerConsumer));
    }

    private void executeComponentAndCodebaseScanners(ConcurrentHashMap<String, Component> componentMap, Consumer<UnaryOperator<Summary>> summaryTransformerConsumer, ComponentMetadata updatedComponentMetadata, Map<Codebase, List<String>> codebaseAndComponentIdsMap) {
        scannerRegistry.getComponentAndCodebaseScanners().forEach(scanner -> executeScanner(
                updatedComponentMetadata,
                getFreshComponentAndCodebaseAndComponentIdsMap(componentMap, codebaseAndComponentIdsMap),
                componentMap,
                scanner,
                summaryTransformerConsumer));
    }

    private void executeLateComponentScanners(ConcurrentHashMap<String, Component> componentMap, Consumer<UnaryOperator<Summary>> summaryTransformerConsumer, ComponentMetadata updatedComponentMetadata) {
        scannerRegistry.getLateComponentScanners().forEach(scanner -> executeScanner(
                updatedComponentMetadata,
                getFreshComponentAndComponentIdMap(componentMap),
                componentMap,
                scanner,
                summaryTransformerConsumer));
    }

    /**
     * The components are immutable value types using Lombok's @Value annotation.  The components in componentMap get updated (replaced because they
     * are immutable) when they pass through the scanners.  Consequently this method needs to be called each time its return value is needed and not stored
     * in a variable for reuse, otherwise the components in the map would be out-of-date for second and subsequent reuses.
     *
     * @param componentMap  a map with component ids as the keys and their components as the values.
     * @return              a map containing components as keys and each component's id as the value
     */
    private Map<Component, List<String>> getFreshComponentAndComponentIdMap(
            ConcurrentHashMap<String, Component> componentMap
    ) {
        return componentMap.values().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.mapping(Component::getId, toList())));
    }

    /**
     * The components are immutable value types using Lombok's @Value annotation.  The components in componentMap get updated (replaced because they
     * are immutable) when they pass through the scanners.  Consequently this method needs to be called each time its return value is needed and not stored
     * in a variable for reuse, otherwise the components in the map would be out-of-date for second and subsequent reuses.
     *
     * @param componentMap a map with component ids as the keys and their components as the values.
     * @return             a map containing repo references as keys and the associated components' ids as the values
     */
    private Map<RepoReference, List<String>> getFreshRepoReferenceAndComponentIdsMap(
            ConcurrentHashMap<String, Component> componentMap
    ) {
        return componentMap.values().stream()
                .filter(this::componentHasRepo)
                .collect(Collectors.groupingBy(Component::getRepo, Collectors.mapping(Component::getId, toList())));
    }

    private boolean componentHasRepo(Component component) {
        return nonNull(component.getRepo());
    }

    /**
     * The components are immutable value types using Lombok's @Value annotation.  The components in componentMap get updated (replaced because they
     * are immutable) when they pass through the scanners.  Consequently this method needs to be called each time its return value is needed and not stored
     * in a variable for reuse, otherwise the components in the map would be out-of-date for second and subsequent reuses.
     *
     * @param componentMap                  a map with component ids as the keys and their components as the values.
     * @param codebaseAndComponentIdsMap    a map with codebases as the keys and their components as the values.  Each codebase can have 1 or more components
     * @return                              a map containing component and codebase pairs as keys and those component's ids as the values
     */
    private Map<ComponentAndCodebase, List<String>> getFreshComponentAndCodebaseAndComponentIdsMap(
            ConcurrentHashMap<String, Component> componentMap,
            Map<Codebase, List<String>> codebaseAndComponentIdsMap
    ) {
        return codebaseAndComponentIdsMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(componentId -> Map.entry(
                                convertComponentIdAndCodebaseToComponentAndCodebase(componentMap, componentId, entry.getKey()),
                                List.of(componentId))))
                .collect(MapCollectors.toMap());
    }

    private ComponentAndCodebase convertComponentIdAndCodebaseToComponentAndCodebase(ConcurrentHashMap<String, Component> componentMap, String componentId,
            Codebase codebase) {

        return new ComponentAndCodebase(componentMap.get(componentId), codebase);
    }

    private <I extends ObjectWithReference, O> Map<O, List<String>> executeScanner(
            ComponentMetadata componentMetadata,
            Map<I, List<String>> inputAndComponentIdsMap,
            ConcurrentHashMap<String, Component> componentMap,
            Scanner<I, O> scanner,
            Consumer<UnaryOperator<Summary>> summaryTransformerConsumer
    ) {
        try {
            scanner.refresh(componentMetadata);
        } catch (Exception e) {
            log.error("Failed to refresh scanner {}", scanner.id(), e);
            List<ScannerError> newErrors = List.of(new ScannerError(scanner.id(), "Failed to refresh scanner",
                    throwableToScannerErrorMapper.map(scanner.id(), e)));
            inputAndComponentIdsMap.values().forEach(componentIds -> addScannerErrorsToComponents(componentMap, componentIds, scanner, newErrors));
            return Map.of();
        }

        Map<O, List<String>> outputAndComponentIdsMap = inputAndComponentIdsMap
                .entrySet()
                .stream()
                .sorted(getInputEntryComparator())
                .map(entry -> executeScanner(entry.getKey(), entry.getValue(), componentMap, scanner))
                .filter(Objects::nonNull)
                .collect(MapCollectors.toMap());

        summaryTransformerConsumer.accept(scanner::transformSummary);

        return outputAndComponentIdsMap;
    }

    private <I extends ObjectWithReference> Comparator<Map.Entry<I, List<String>>> getInputEntryComparator() {
        return Comparator.comparing(entry -> entry.getKey().reference());
    }

    private void addScannerErrorsToComponents(ConcurrentHashMap<String, Component> componentMap, List<String> componentIds, Scanner<?, ?> scanner,
            List<ScannerError> newErrors) {
        updateComponents(componentMap, componentIds, scanner, component -> addScannerErrorsToComponent(component, newErrors));
    }

    private Component addScannerErrorsToComponent(Component component, List<ScannerError> newErrors) {
        List<ScannerError> errors = new ArrayList<>(component.getScannerErrors());
        errors.addAll(newErrors);
        return component.withScannerErrors(errors);
    }

    private void updateComponents(ConcurrentHashMap<String, Component> componentMap, List<String> componentIds, Scanner<?, ?> scanner,
            UnaryOperator<Component> componentTransformer) {
        componentIds.forEach(componentId -> updateComponent(componentMap, componentId, scanner, componentTransformer));
    }

    private void updateComponent(ConcurrentHashMap<String, Component> componentMap, String componentId, Scanner<?, ?> scanner, UnaryOperator<Component> componentTransformer) {
        Component component = componentMap.get(componentId);
        try {
            component = componentTransformer.apply(component);
        } catch (Exception e) {
            log.error("Scanner {} failed to update component", scanner.id(), e);
            ScannerError scannerError = new ScannerError(scanner.id(), "Component update failed",
                    throwableToScannerErrorMapper.map(scanner.id(), e));
            component = addScannerErrorsToComponent(component, List.of(scannerError));
        }
        try {
            validatorService.validate(component);
        } catch (ValidationException e) {
            ScannerError scannerError = new ScannerError(scanner.id(), "Validation failure for transformed component",
                    throwableToScannerErrorMapper.map(scanner.id(), e));
            component = addScannerErrorsToComponent(component, List.of(scannerError));
        }
        componentMap.put(componentId, component);
    }

    private <I extends ObjectWithReference, O> Map.Entry<O, List<String>> executeScanner(
            I input, 
            List<String> componentIds,
            ConcurrentHashMap<String, Component> componentMap, 
            Scanner<I, O> scanner
    ) {
        Output<O, Component> output = executor.executeScanner(scanner, input.reference(), input);

        if (output.failed()) {
            addScannerErrorsToComponents(componentMap, componentIds, scanner, output.getErrors());
            output.getErrors().forEach(error -> log.error(
                    "Failed to scan \"{}\" with scanner {}: {}", 
                    StringEscapeUtils.escapeString(input.reference()), 
                    scanner.id(),
                    error.toString()
            ));
        }

        UnaryOperator<Component> transformer = output.getTransformer();
        if (nonNull(transformer)) {
            updateComponents(componentMap, componentIds, scanner, transformer);
        }

        return output.mapOutput(it -> Map.entry(it, componentIds)).orElse(null);
    }
}
