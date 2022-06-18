package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.sdk.models.*;
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
            List<TestOutcome> testOutcomes
    ) {
        return filteredComponents(
                componentRepository.getComponents(),
                offset,
                limit,
                stateTypes,
                testOutcomes
        );
    }

    public Component getComponent(
            String componentId,
            List<String> stateTypes,
            List<TestOutcome> testOutcomes
    ) {
        Component component = componentRepository.getComponent(componentId);
        if (isNull(component)) {
            return null;
        }
        return filterComponent(
                component,
                stateTypes,
                testOutcomes
        );
    }
    public List<Diagram> getComponentDiagrams(String componentId) {
        return componentRepository.getComponentDiagrams(componentId);
    }

    public List<Diagram> getDiagrams() {
        return componentRepository.getDiagrams();
    }

    public Diagram getDiagram(String diagramId, List<String> stateTypes) {
        Diagram diagram = componentRepository.getDiagram(diagramId);
        if (isNull(diagram)) {
            return null;
        }
        return diagram.withStates(filterStates(
                diagram.getStates(),
                stateTypes
        ));
    }

    public List<Team> getTeams(
            List<String> stateTypes,
            List<TestOutcome> testOutcomes
    ) {
        return filterComponentsForTeams(
                componentRepository.getTeams(),
                stateTypes,
                testOutcomes
        );
    }

    public Team getTeam(
            String teamId,
            List<String> stateTypes,
            List<TestOutcome> testOutcomes
    ) {
        return filterComponentsForTeam(
                componentRepository.getTeam(teamId),
                stateTypes,
                testOutcomes
        );
    }

    public List<Area> getAreas(
            List<String> stateTypes,
            List<TestOutcome> testOutcomes
    ) {
        return filterComponentsForAreas(
                componentRepository.getAreas(),
                stateTypes,
                testOutcomes
        );
    }

    public Area getArea(
            String areaId,
            List<String> stateTypes,
            List<TestOutcome> testOutcomes
    ) {
        return filterComponentsForArea(
                componentRepository.getArea(areaId),
                stateTypes,
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
            List<TestOutcome> testOutcomes
    ) {
        return teams.stream()
                .map(team -> filterComponentsForTeam(team, stateTypes, testOutcomes))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private Team filterComponentsForTeam(
            Team team,
            List<String> stateTypes,
            List<TestOutcome> testOutcomes
    ) {
        if (isNull(team)) {
            return null;
        }

        return team.withComponents(filteredComponents(
                team.getComponents(),
                stateTypes,
                testOutcomes
        ));
    }

    private List<Area> filterComponentsForAreas(
            List<Area> areas,
            List<String> stateTypes,
            List<TestOutcome> testOutcomes
    ) {
        return areas.stream()
                .map(area -> filterComponentsForArea(area, stateTypes, testOutcomes))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private Area filterComponentsForArea(
            Area area,
            List<String> stateTypes,
            List<TestOutcome> testOutcomes
    ) {
        if (isNull(area)) {
            return null;
        }

        return area.withComponents(filteredComponents(
                area.getComponents(),
                stateTypes,
                testOutcomes
        ));
    }

    private List<Component> filteredComponents(
            List<Component> components,
            List<String> stateTypes,
            List<TestOutcome> testOutcomes
    ) {
        return filteredComponents(
                components,
                Optional.empty(),
                Optional.empty(),
                stateTypes,
                testOutcomes
        );
    }

    private List<Component> filteredComponents(
            List<Component> components,
            Optional<Integer> offset,
            Optional<Integer> limit,
            List<String> stateTypes,
            List<TestOutcome> testOutcomes
    ) {
        Stream<Component> componentStream = components.stream();

        if (offset.isPresent()) {
            componentStream = componentStream.skip(offset.get());
        }

        if (limit.isPresent()) {
            componentStream = componentStream.limit(limit.get());
        }

        return componentStream
                .map(component -> filterComponent(component, stateTypes, testOutcomes))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private Component filterComponent(
            Component component,
            List<String> stateTypes,
            List<TestOutcome> testOutcomes
    ) {
        if (nonNull(testOutcomes) && !testOutcomes.isEmpty()) {
            List<TestResult> testResults = filterTestResults(component.getTestResults(), testOutcomes);

            if (testResults.isEmpty()) {
                return null;
            }

            component = component.withTestResults(testResults);
        }

        return component.withStates(filterStates(component.getStates(), stateTypes));
    }

    private <T extends ObjectWithType> List<T> filterStates(List<T> states, List<String> stateTypes) {
        if (isNull(stateTypes) || stateTypes.isEmpty()) {
            return states;
        }
        return states.stream()
                .filter(state -> stateTypes.contains(state.getType()))
                .collect(toUnmodifiableList());
    }
    
    private List<TestResult> filterTestResults(List<TestResult> testResults, List<TestOutcome> testOutcomes) {
        return testResults.stream()
                .filter(testResult -> testOutcomes.contains(testResult.getOutcome()))
                .collect(toUnmodifiableList());
    }
}
