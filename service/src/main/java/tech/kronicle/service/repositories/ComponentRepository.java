package tech.kronicle.service.repositories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import tech.kronicle.sdk.models.*;
import tech.kronicle.sdk.models.doc.DocFile;
import tech.kronicle.service.services.*;
import tech.kronicle.utils.ObjectReference;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ComponentRepository extends RefreshingRepository {

    private final ComponentMetadataRepository repository;
    private final ComponentMetadataLoader loader;
    private final ComponentMetadataAssembler assembler;
    private final ScanEngine scanEngine;
    private final ScannerExtensionRegistry scannerRegistry;
    private final TestEngine testEngine;
    private final TestFinder testFinder;
    private volatile ConcurrentHashMap<String, Area> areas = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<String, Team> teams = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<String, Component> components = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<String, Diagram> diagrams = new ConcurrentHashMap<>();
    private volatile Summary summary = Summary.EMPTY;

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected void doInitialize() {
    }

    @Scheduled(cron = "0 */1 * * * *", zone = "UTC")
    @Override
    public void refresh() {
        super.refresh();
    }

    @Override
    protected void doRefresh(boolean firstTime) {
        ComponentMetadata componentMetadata = repository.getComponentMetadata();
        ComponentMetadataLoader.Output loaderOutput = loader.loadComponentMetadata(componentMetadata);
        Consumer<Summary> summaryUpdater;
        ObjectReference<Summary> newSummary = new ObjectReference<>();

        if (firstTime) {
            updateState(loaderOutput);
            summaryUpdater = updatedSummary -> summary = updatedSummary;
        } else {
            summaryUpdater = newSummary::set;
        }

        scanEngine.scan(componentMetadata, loaderOutput.getComponents(), loaderOutput.getDiagrams(), summaryUpdater);
        testEngine.test(loaderOutput.getComponents());

        if (!firstTime) {
            updateState(loaderOutput);
            summary = newSummary.get();
        }
    }

    private void updateState(ComponentMetadataLoader.Output loaderOutput) {
        areas = loaderOutput.getAreas();
        teams = loaderOutput.getTeams();
        components = loaderOutput.getComponents();
        diagrams = loaderOutput.getDiagrams();
    }

    public List<Area> getAreas() {
        return assembler.toSortedUnmodifiableAreaList(areas.values().stream(), teams, components);
    }

    public Area getArea(String areaId) {
        return assembler.addNestedItemsToArea(areas.get(areaId), teams, components);
    }

    public List<Team> getTeams() {
        return assembler.toSortedUnmodifiableTeamList(teams.values().stream(), components);
    }

    public Team getTeam(String teamId) {
        return assembler.addNestedItemsToTeam(teams.get(teamId), components);
    }

    public boolean hasComponents() {
        return !components.isEmpty();
    }

    public List<Component> getComponents() {
        return assembler.toSortedUnmodifiableComponentList(components.values().stream());
    }

    public Component getComponent(String componentId) {
        return components.get(componentId);
    }

    public List<Diagram> getComponentDiagrams(String componentId) {
        return diagrams.values().stream()
                .filter(diagramIncludesComponent(componentId))
                .sorted(Comparator.comparing(Diagram::getId))
                .collect(toUnmodifiableList());
    }

    public List<Diagram> getDiagrams() {
        return assembler.toSortedUnmodifiableDiagramList(diagrams.values().stream());
    }

    public Diagram getDiagram(String diagramId) {
        return diagrams.get(diagramId);
    }

    public Summary getSummary() {
        return summary;
    }

    public List<Scanner> getScanners() {
        return scannerRegistry.getAllItems().stream()
                .map(this::mapScanner)
                .sorted(Comparator.comparing(Scanner::getId))
                .collect(toUnmodifiableList());
    }

    public Scanner getScanner(String scannerId) {
        return mapScanner(scannerRegistry.getItem(scannerId));
    }

    public List<Test> getTests() {
        return testFinder.getAllTests().stream()
                .map(this::mapTest)
                .sorted(Comparator.comparing(Test::getId))
                .collect(toUnmodifiableList());
    }

    public Test getTest(String testId) {
        return Optional.ofNullable(testFinder.getTest(testId))
                .map(this::mapTest)
                .orElse(null);
    }

    private Scanner mapScanner(tech.kronicle.pluginapi.scanners.Scanner<?, ?> scanner) {
        if (isNull(scanner)) {
            return null;
        }

        return Scanner.builder()
                .id(scanner.id())
                .description(scanner.description())
                .notes(scanner.notes())
                .build();
    }

    private Predicate<Diagram> diagramIncludesComponent(String componentId) {
        return diagram -> diagram.getConnections().stream().anyMatch(connectionIncludesComponent(componentId));
    }

    private Predicate<DiagramConnection> connectionIncludesComponent(String componentId) {
        return connection -> Objects.equals(connection.getSourceComponentId(), componentId) ||
                Objects.equals(connection.getTargetComponentId(), componentId);
    }

    private Test mapTest(tech.kronicle.service.tests.Test<?> test) {
        return new Test(test.id(), test.description(), test.notes(), test.priority());
    }
}
