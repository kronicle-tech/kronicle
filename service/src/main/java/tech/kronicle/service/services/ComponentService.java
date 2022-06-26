package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.sdk.constants.ComponentStateTypes;
import tech.kronicle.sdk.models.*;
import tech.kronicle.sdk.models.doc.DocFile;
import tech.kronicle.sdk.models.doc.DocState;
import tech.kronicle.service.repositories.ComponentRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@Service
@RequiredArgsConstructor
public class ComponentService {

    private final ComponentRepository componentRepository;

    public List<Component> getComponents(
            Optional<Integer> offset,
            Optional<Integer> limit,
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        return filterComponents(
                componentRepository.getComponents(),
                offset,
                limit,
                stateTypes,
                stateIds,
                testOutcomes
        );
    }

    public Component getComponent(
            String componentId,
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        Component component = componentRepository.getComponent(componentId);
        if (isNull(component)) {
            return null;
        }
        return filterComponent(
                component,
                stateTypes,
                stateIds,
                testOutcomes
        );
    }
    public List<Diagram> getComponentDiagrams(String componentId) {
        return componentRepository.getComponentDiagrams(componentId);
    }

    public List<Diagram> getDiagrams(
            List<String> stateTypes,
            List<String> stateIds
    ) {
        return filterDiagrams(componentRepository.getDiagrams(), stateTypes, stateIds);
    }

    public Diagram getDiagram(
            String diagramId,
            List<String> stateTypes,
            List<String> stateIds
    ) {
        Diagram diagram = componentRepository.getDiagram(diagramId);
        if (isNull(diagram)) {
            return null;
        }
        return filterDiagram(diagram, stateTypes, stateIds);
    }

    public List<Team> getTeams(
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        return filterComponentsForTeams(
                componentRepository.getTeams(),
                stateTypes,
                stateIds,
                testOutcomes
        );
    }

    public Team getTeam(
            String teamId,
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        return filterComponentsForTeam(
                componentRepository.getTeam(teamId),
                stateTypes,
                stateIds,
                testOutcomes
        );
    }

    public List<Area> getAreas(
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        return filterComponentsForAreas(
                componentRepository.getAreas(),
                stateTypes,
                stateIds,
                testOutcomes
        );
    }

    public Area getArea(
            String areaId,
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        return filterComponentsForArea(
                componentRepository.getArea(areaId),
                stateTypes,
                stateIds,
                testOutcomes
        );
    }

    public List<Scanner> getScanners() {
        return componentRepository.getScanners();
    }

    public Scanner getScanner(String scannerId) {
        return componentRepository.getScanner(scannerId);
    }

    public List<Test> getTests() {
        return componentRepository.getTests();
    }

    public Test getTest(String testId) {
        return componentRepository.getTest(testId);
    }

    public Summary getSummary() {
        return componentRepository.getSummary();
    }

    private List<Team> filterComponentsForTeams(
            List<Team> teams,
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        return teams.stream()
                .map(team -> filterComponentsForTeam(team, stateTypes, stateIds, testOutcomes))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private Team filterComponentsForTeam(
            Team team,
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        if (isNull(team)) {
            return null;
        }

        return team.withComponents(filterComponents(
                team.getComponents(),
                stateTypes,
                stateIds,
                testOutcomes
        ));
    }

    private List<Area> filterComponentsForAreas(
            List<Area> areas,
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        return areas.stream()
                .map(area -> filterComponentsForArea(area, stateTypes, stateIds, testOutcomes))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private Area filterComponentsForArea(
            Area area,
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        if (isNull(area)) {
            return null;
        }

        return area.withComponents(filterComponents(
                area.getComponents(),
                stateTypes,
                stateIds,
                testOutcomes
        ));
    }

    private List<Component> filterComponents(
            List<Component> components,
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        return filterComponents(
                components,
                Optional.empty(),
                Optional.empty(),
                stateTypes,
                stateIds,
                testOutcomes
        );
    }

    private List<Component> filterComponents(
            List<Component> components,
            Optional<Integer> offset,
            Optional<Integer> limit,
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        Stream<Component> stream = components.stream();

        if (offset.isPresent()) {
            stream = stream.skip(offset.get());
        }

        if (limit.isPresent()) {
            stream = stream.limit(limit.get());
        }

        return stream
                .map(component -> filterComponent(component, stateTypes, stateIds, testOutcomes))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private Component filterComponent(
            Component component,
            List<String> stateTypes,
            List<String> stateIds,
            List<TestOutcome> testOutcomes
    ) {
        if (nonNull(testOutcomes) && !testOutcomes.isEmpty()) {
            List<TestResult> testResults = filterTestResults(component.getTestResults(), testOutcomes);

            if (testResults.isEmpty()) {
                return null;
            }

            component = component.withTestResults(testResults);
        }

        return component.withStates(filterStates(component.getStates(), stateTypes, stateIds));
    }

    private List<Diagram> filterDiagrams(
            List<Diagram> diagrams,
            List<String> stateTypes,
            List<String> stateIds
    ) {
        return diagrams.stream()
                .map(diagram -> filterDiagram(diagram, stateTypes, stateIds))
                .collect(toUnmodifiableList());
    }

    private Diagram filterDiagram(
            Diagram diagram,
            List<String> stateTypes,
            List<String> stateIds
    ) {
        return diagram.withStates(filterStates(
                diagram.getStates(),
                stateTypes,
                stateIds
        ));
    }

    private <T extends State> List<T> filterStates(
            List<T> states,
            List<String> stateTypes,
            List<String> stateIds
    ) {
        Stream<T> stream = states.stream();

        if (!stateTypes.isEmpty()) {
            stream = stream.filter(state -> stateTypes.contains(state.getType()));
        }

        if (!stateIds.isEmpty()) {
            stream = stream.filter(state -> nonNull(state.getId()) && stateIds.contains(state.getId()));
        }

        return stream.collect(toUnmodifiableList());
    }
    
    private List<TestResult> filterTestResults(List<TestResult> testResults, List<TestOutcome> testOutcomes) {
        return testResults.stream()
                .filter(testResult -> testOutcomes.contains(testResult.getOutcome()))
                .collect(toUnmodifiableList());
    }

    public DocFile getComponentDocFile(String componentId, String docId, String docFilePath) {
        Component component = getComponent(componentId, List.of(ComponentStateTypes.DOC), List.of(docId), List.of());
        if (isNull(component)) {
            return null;
        }
        List<DocState> docs = component.getStates(ComponentStateTypes.DOC);
        if (docs.size() != 1) {
            return null;
        }
        DocState doc = docs.get(0);
        List<DocFile> docFiles = doc.getFiles().stream()
                .filter(docFile -> Objects.equals(docFile.getPath(), docFilePath))
                .collect(toUnmodifiableList());
        if (docFiles.size() != 1) {
            return null;
        }
        return docFiles.get(0);
    }
}
