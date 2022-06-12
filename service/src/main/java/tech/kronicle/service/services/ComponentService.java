package tech.kronicle.service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.sdk.models.*;
import tech.kronicle.service.repositories.ComponentRepository;
import tech.kronicle.utils.ObjectReference;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@Service
@RequiredArgsConstructor
public class ComponentService {

    private final ComponentRepository componentRepository;

    public List<Component> getComponents(Optional<Integer> offset, Optional<Integer> limit, List<TestOutcome> testOutcomes) {
        return getFilteredComponents(componentRepository.getComponents(), offset, limit, testOutcomes);
    }

    public Component getComponent(String componentId, List<String> stateTypes) {
        Component component = componentRepository.getComponent(componentId);
        if (isNull(component)) {
            return null;
        }
        return component.withStates(getFilteredStates(component.getStates(), stateTypes));
    }

    public List<SummarySubComponentDependencyNode> getComponentNodes(String componentId) {
        return componentRepository.getComponentNodes(componentId);
    }

    public List<SummaryCallGraph> getComponentCallGraphs(String componentId) {
        return componentRepository.getComponentCallGraphs(componentId);
    }

    public List<Diagram> getComponentDiagrams(String componentId) {
        return componentRepository.getComponentDiagrams(componentId);
    }

    public List<Team> getTeams(List<TestOutcome> testOutcomes) {
        return filterComponentsForTeams(componentRepository.getTeams(), testOutcomes);
    }

    public Team getTeam(String teamId, List<TestOutcome> testOutcomes) {
        return filterComponentsForTeam(componentRepository.getTeam(teamId), testOutcomes);
    }

    public List<Area> getAreas(List<TestOutcome> testOutcomes) {
        return filterComponentsForAreas(componentRepository.getAreas(), testOutcomes);
    }

    public Area getArea(String areaId, List<TestOutcome> testOutcomes) {
        return filterComponentsForArea(componentRepository.getArea(areaId), testOutcomes);
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

    private List<Team> filterComponentsForTeams(List<Team> teams, List<TestOutcome> testOutcomes) {
        return teams.stream()
                .map(team -> filterComponentsForTeam(team, testOutcomes))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private Team filterComponentsForTeam(Team team, List<TestOutcome> testOutcomes) {
        if (isNull(team)) {
            return null;
        }

        return team.withComponents(getFilteredComponents(team.getComponents(), testOutcomes));
    }

    private List<Area> filterComponentsForAreas(List<Area> areas, List<TestOutcome> testOutcomes) {
        return areas.stream()
                .map(area -> filterComponentsForArea(area, testOutcomes))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private Area filterComponentsForArea(Area area, List<TestOutcome> testOutcomes) {
        if (isNull(area)) {
            return null;
        }

        return area.withComponents(getFilteredComponents(area.getComponents(), testOutcomes));
    }

    private List<Component> getFilteredComponents(List<Component> components, List<TestOutcome> testOutcomes) {
        return getFilteredComponents(components, Optional.empty(), Optional.empty(), testOutcomes);
    }

    private List<Component> getFilteredComponents(List<Component> components, Optional<Integer> offset, Optional<Integer> limit, 
            List<TestOutcome> testOutcomes) {
        ObjectReference<Stream<Component>> componentStream = new ObjectReference<>(components.stream());

        offset.ifPresent(offsetValue -> componentStream.set(componentStream.get().skip(offsetValue)));
        limit.ifPresent(limitValue -> componentStream.set(componentStream.get().limit(limitValue)));

        if (nonNull(testOutcomes) && !testOutcomes.isEmpty()) {
            componentStream.set(componentStream.get()
                    .map(filterComponentBasedOnTestOutcomes(testOutcomes))
                    .filter(Objects::nonNull));
        }

        return componentStream.get()
                .collect(toUnmodifiableList());
    }

    private UnaryOperator<Component> filterComponentBasedOnTestOutcomes(List<TestOutcome> testOutcomes) {
        return component -> {
            List<TestResult> testResults = filterTestResultsBasedOnTestOutcomes(component.getTestResults(), testOutcomes);

            if (testResults.isEmpty()) {
                return null;
            }

            return component.withTestResults(testResults);
        };
    }

    private List<ComponentState> getFilteredStates(List<ComponentState> states, List<String> stateTypes) {
        if (isNull(stateTypes) || stateTypes.isEmpty()) {
            return states;
        }
        return states.stream()
                .filter(state -> stateTypes.contains(state.getType()))
                .collect(toUnmodifiableList());
    }
    
    private List<TestResult> filterTestResultsBasedOnTestOutcomes(List<TestResult> testResults, List<TestOutcome> testOutcomes) {
        return testResults.stream()
                .filter(testResult -> testOutcomes.contains(testResult.getOutcome()))
                .collect(toUnmodifiableList());
    }
}
