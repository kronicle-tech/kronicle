package tech.kronicle.service.services;

import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.*;
import tech.kronicle.service.repositories.ComponentRepository;
import tech.kronicle.service.testutils.FakeDiagramState;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.service.testutils.DiagramStateUtils.createDiagramState;
import static tech.kronicle.service.testutils.DiagramUtils.createDiagram;

@ExtendWith(MockitoExtension.class)
public class ComponentServiceTest {

    @Mock
    private ComponentRepository mockComponentRepository;
    private ComponentService underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new ComponentService(mockComponentRepository);
    }
    
    @Test
    public void getComponentsShouldReturnAllComponents() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .build();
        when(mockComponentRepository.getComponents()).thenReturn(List.of(component1, component2));

        // When
        List<Component> returnValue = underTest.getComponents(Optional.empty(), Optional.empty(), List.of());

        // Then
        assertThat(returnValue).containsExactly(component1, component2);
    }

    @Test
    public void getComponentsShouldReturnComponentsFromOffset() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .testResults(List.of(new TestResult(null, TestOutcome.FAIL, null, null)))
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .build();
        Component component3= Component.builder()
                .id("test-component-id-3")
                .build();
        when(mockComponentRepository.getComponents()).thenReturn(List.of(component1, component2, component3));

        // When
        List<Component> returnValue = underTest.getComponents(Optional.of(1), Optional.empty(), List.of());

        // Then
        assertThat(returnValue).containsExactly(component2, component3);
    }

    @Test
    public void getComponentsShouldIgnoreOffsetBeyondSize() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .build();
        List<Component> components = List.of(component1, component2);
        when(mockComponentRepository.getComponents()).thenReturn(components);

        // When
        List<Component> returnValue = underTest.getComponents(Optional.of(components.size() + 1), Optional.empty(), List.of());

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void getComponentsShouldReturnComponentsUpToLimit() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .build();
        Component component3 = Component.builder()
                .id("test-component-id-3")
                .build();
        when(mockComponentRepository.getComponents()).thenReturn(List.of(component1, component2, component3));

        // When
        List<Component> returnValue = underTest.getComponents(Optional.empty(), Optional.of(2), List.of());

        // Then
        assertThat(returnValue).containsExactly(component1, component2);
    }

    @Test
    public void getComponentsShouldReturnComponentsFromOffsetUpToLimit() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .build();
        Component component3 = Component.builder()
                .id("test-component-id-3")
                .build();
        Component component4 = Component.builder()
                .id("test-component-id-4")
                .build();
        when(mockComponentRepository.getComponents()).thenReturn(List.of(component1, component2, component3, component4));

        // When
        List<Component> returnValue = underTest.getComponents(Optional.of(1), Optional.of(2), List.of());

        // Then
        assertThat(returnValue).containsExactly(component2, component3);
    }

    @Test
    public void getComponentsShouldReturnComponentsFilteredByAllFilters() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .testResults(List.of(new TestResult(null, TestOutcome.FAIL, null, null)))
                .build();
        Component component3 = Component.builder()
                .id("test-component-id-3")
                .testResults(List.of(new TestResult(null, TestOutcome.PASS, null, null)))
                .build();
        Component component4 = Component.builder()
                .id("test-component-id-4")
                .build();
        when(mockComponentRepository.getComponents()).thenReturn(List.of(component1, component2, component3, component4));

        // When
        List<Component> returnValue = underTest.getComponents(Optional.of(1), Optional.of(2), List.of(TestOutcome.FAIL));

        // Then
        assertThat(returnValue).containsExactly(component2);
    }

    @Test
    public void getComponentShouldReturnTheComponentWithMatchingId() {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id")
                .build();
        when(mockComponentRepository.getComponent(component1.getId())).thenReturn(component1);

        // When
        Component returnValue = underTest.getComponent(component1.getId(), List.of());

        // Then
        assertThat(returnValue).isSameAs(component1);
    }

    @Test
    public void getComponentShouldReturnNullIfNoComponentMatchesId() {
        // Given
        String componentId = "test-component-id";
        when(mockComponentRepository.getComponent(componentId)).thenReturn(null);

        // When
        Component returnValue = underTest.getComponent(componentId, List.of());

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getComponentShouldReturnAllStatesIfStateTypesListIsNull() {
        // Given
        String componentId = "test-component-id";
        Component component = Component.builder()
                .id(componentId)
                .states(List.of(
                        new TestComponentState(1),
                        new TestComponentState(2)
                ))
                .build();
        when(mockComponentRepository.getComponent(componentId)).thenReturn(component);

        // When
        Component returnValue = underTest.getComponent(componentId, null);

        // Then
        assertThat(returnValue).isEqualTo(component);
    }

    @Test
    public void getComponentShouldReturnAllStatesIfStateTypesListIsEmpty() {
        // Given
        String componentId = "test-component-id";
        Component component = Component.builder()
                .id(componentId)
                .states(List.of(
                        new TestComponentState(1),
                        new TestComponentState(2)
                ))
                .build();
        when(mockComponentRepository.getComponent(componentId)).thenReturn(component);

        // When
        Component returnValue = underTest.getComponent(componentId, List.of());

        // Then
        assertThat(returnValue).isEqualTo(component);
    }

    @Test
    public void getComponentShouldFilterByStateTypesIfAtLeastOneStateTypeIsSpecified() {
        // Given
        String componentId = "test-component-id";
        TestComponentState state1 = new TestComponentState(1);
        TestComponentState state2 = new TestComponentState(2);
        TestComponentState state3 = new TestComponentState(3);
        TestComponentState state4 = new TestComponentState(4);
        Component component = Component.builder()
                .id(componentId)
                .states(List.of(
                        state1,
                        state2,
                        state3,
                        state4
                ))
                .build();
        when(mockComponentRepository.getComponent(componentId)).thenReturn(component);

        // When
        Component returnValue = underTest.getComponent(componentId, List.of(
                state1.type,
                state3.type
        ));

        // Then
        assertThat(returnValue).isEqualTo(
                component.withStates(List.of(
                        state1,
                        state3
                ))
        );
    }

    @Test
    public void getComponentDiagramsShouldReturnTheComponentDiagramsForAComponent() {
        // Given
        String componentId = "test-component-id-1";
        Diagram diagram1 = createDiagram(1);
        Diagram diagram2 = createDiagram(2);
        List<Diagram> diagrams = List.of(diagram1, diagram2);
        when(mockComponentRepository.getComponentDiagrams(componentId)).thenReturn(diagrams);

        // When
        List<Diagram> returnValue = underTest.getComponentDiagrams(componentId);

        // Then
        assertThat(returnValue).isSameAs(diagrams);
    }

    @Test
    public void getDiagramsShouldReturnAllDiagrams() {
        // Given
        Diagram diagram1 = createDiagram(1);
        Diagram diagram2 = createDiagram(2);
        when(mockComponentRepository.getDiagrams()).thenReturn(List.of(diagram1, diagram2));

        // When
        List<Diagram> returnValue = underTest.getDiagrams();

        // Then
        assertThat(returnValue).containsExactly(diagram1, diagram2);
    }

    @Test
    public void getDiagramShouldReturnTheDiagramWithMatchingId() {
        // Given
        Diagram diagram1 = Diagram.builder()
                .id("test-diagram-id")
                .build();
        when(mockComponentRepository.getDiagram(diagram1.getId())).thenReturn(diagram1);

        // When
        Diagram returnValue = underTest.getDiagram(diagram1.getId(), List.of());

        // Then
        assertThat(returnValue).isSameAs(diagram1);
    }

    @Test
    public void getDiagramShouldReturnNullIfNoDiagramMatchesId() {
        // Given
        String diagramId = "test-diagram-id";
        when(mockComponentRepository.getDiagram(diagramId)).thenReturn(null);

        // When
        Diagram returnValue = underTest.getDiagram(diagramId, List.of());

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getDiagramShouldReturnAllStatesIfStateTypesListIsNull() {
        // Given
        String diagramId = "test-diagram-id";
        Diagram diagram = Diagram.builder()
                .id(diagramId)
                .states(List.of(
                        createDiagramState(1),
                        createDiagramState(1)
                ))
                .build();
        when(mockComponentRepository.getDiagram(diagramId)).thenReturn(diagram);

        // When
        Diagram returnValue = underTest.getDiagram(diagramId, null);

        // Then
        assertThat(returnValue).isEqualTo(diagram);
    }

    @Test
    public void getDiagramShouldReturnAllStatesIfStateTypesListIsEmpty() {
        // Given
        String diagramId = "test-diagram-id";
        Diagram diagram = Diagram.builder()
                .id(diagramId)
                .states(List.of(
                        createDiagramState(1),
                        createDiagramState(2)
                ))
                .build();
        when(mockComponentRepository.getDiagram(diagramId)).thenReturn(diagram);

        // When
        Diagram returnValue = underTest.getDiagram(diagramId, List.of());

        // Then
        assertThat(returnValue).isEqualTo(diagram);
    }

    @Test
    public void getDiagramShouldFilterByStateTypesIfAtLeastOneStateTypeIsSpecified() {
        // Given
        String diagramId = "test-diagram-id";
        FakeDiagramState state1 = createDiagramState(1);
        FakeDiagramState state2 = createDiagramState(2);
        FakeDiagramState state3 = createDiagramState(3);
        FakeDiagramState state4 = createDiagramState(4);
        Diagram diagram = Diagram.builder()
                .id(diagramId)
                .states(List.of(
                        state1,
                        state2,
                        state3,
                        state4
                ))
                .build();
        when(mockComponentRepository.getDiagram(diagramId)).thenReturn(diagram);

        // When
        Diagram returnValue = underTest.getDiagram(diagramId, List.of(
                state1.getType(),
                state3.getType()
        ));

        // Then
        assertThat(returnValue).isEqualTo(
                diagram.withStates(List.of(
                        state1,
                        state3
                ))
        );
    }

    @Test
    public void getTeamsShouldReturnAllTeams() {
        // Given
        Team team1 = Team.builder()
                .id("test-team-id-1")
                .build();
        Team team2 = Team.builder()
                .id("test-team-id-2")
                .build();
        List<Team> teams = List.of(team1, team2);
        when(mockComponentRepository.getTeams()).thenReturn(teams);

        // When
        List<Team> returnValue = underTest.getTeams(List.of());

        // Then
        assertThat(returnValue).containsExactly(team1, team2);
    }

    @Test
    public void getTeamShouldReturnTheTeamWithMatchingId() {
        // Given
        Team team1 = Team.builder()
                .id("test-team-id")
                .build();
        when(mockComponentRepository.getTeam(team1.getId())).thenReturn(team1);

        // When
        Team returnValue = underTest.getTeam(team1.getId(), List.of());

        // Then
        assertThat(returnValue).isEqualTo(team1);
    }

    @Test
    public void getTeamShouldReturnNullIfNoTeamMatchesId() {
        // Given
        String teamId = "test-team-id";
        when(mockComponentRepository.getTeam(teamId)).thenReturn(null);

        // When
        Team returnValue = underTest.getTeam(teamId, List.of());

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getAreasShouldReturnAllAreas() {
        // Given
        Area area1 = Area.builder()
                .id("test-area-id-1")
                .build();
        Area area2 = Area.builder()
                .id("test-area-id-2")
                .build();
        List<Area> areas = List.of(area1, area2);
        when(mockComponentRepository.getAreas()).thenReturn(areas);

        // When
        List<Area> returnValue = underTest.getAreas(List.of());

        // Then
        assertThat(returnValue).containsExactly(area1, area2);
    }

    @Test
    public void getAreaShouldReturnTheAreaWithMatchingId() {
        // Given
        Area area1 = Area.builder()
                .id("test-area-id")
                .build();
        when(mockComponentRepository.getArea(area1.getId())).thenReturn(area1);

        // When
        Area returnValue = underTest.getArea(area1.getId(), List.of());

        // Then
        assertThat(returnValue).isEqualTo(area1);
    }

    @Test
    public void getAreaShouldReturnNullIfNoAreaMatchesId() {
        // Given
        String areaId = "test-area-id";
        when(mockComponentRepository.getArea(areaId)).thenReturn(null);

        // When
        Area returnValue = underTest.getArea(areaId, List.of());

        // Then
        assertThat(returnValue).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideComponentsReturningMethods")
    public void componentsReturningMethodsShouldReturnAllComponentsWhenNoTestResultFilterIsSpecified(ComponentsReturningMethod methodWrapper) {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .testResults(List.of(TestResult.builder().outcome(TestOutcome.FAIL).build()))
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .build();
        List<Component> components = List.of(component1, component2);

        // When
        List<Component> returnValue = methodWrapper.call(underTest, mockComponentRepository, components, List.of());

        // Then
        assertThat(returnValue).containsExactly(component1, component2);
    }

    @ParameterizedTest
    @MethodSource("provideComponentsReturningMethods")
    public void componentsReturningMethodsShouldNotReturnComponentWithEmptyTestResultsWhenTestResultFilterIsSpecified(ComponentsReturningMethod methodWrapper) {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .testResults(List.of())
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .testResults(List.of(new TestResult("test-id-1", TestOutcome.PASS, null, null)))
                .build();
        List<Component> components = List.of(component1, component2);

        // When
        List<Component> returnValue = methodWrapper.call(underTest, mockComponentRepository, components, List.of(TestOutcome.PASS));

        // Then
        assertThat(returnValue).containsExactly(component2);
    }

    @ParameterizedTest
    @MethodSource("provideComponentsReturningMethods")
    public void componentsReturningMethodsShouldNotReturnComponentWithAnUnwantedTestResultWhenTestResultFilterIsSpecified(ComponentsReturningMethod methodWrapper) {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .testResults(List.of(new TestResult("test-id-1", TestOutcome.FAIL, null, null)))
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .testResults(List.of(new TestResult("test-id-2", TestOutcome.PASS, null, null)))
                .build();
        List<Component> components = List.of(component1, component2);

        // When
        List<Component> returnValue = methodWrapper.call(underTest, mockComponentRepository, components, List.of(TestOutcome.PASS));

        // Then
        assertThat(returnValue).containsExactly(component2);
    }

    @ParameterizedTest
    @MethodSource("provideComponentsReturningMethods")
    public void componentsReturningMethodsShouldReturnOnlyTestResultsOfAComponentThatMatchTestOutcomeFilter(ComponentsReturningMethod methodWrapper) {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .testResults(List.of(
                        new TestResult("test-id-1", TestOutcome.FAIL, null, null),
                        new TestResult("test-id-2", TestOutcome.PASS, null, null),
                        new TestResult("test-id-3", TestOutcome.NOT_APPLICABLE, null, null)))
                .build();
        List<Component> components = List.of(component1);

        // When
        List<Component> returnValue = methodWrapper.call(underTest, mockComponentRepository, components, List.of(TestOutcome.PASS));

        // Then
        assertThat(returnValue).containsExactly(Component.builder()
                .id("test-component-id-1")
                .testResults(List.of(
                        new TestResult("test-id-2", TestOutcome.PASS, null, null)))
                .build());
    }

    @ParameterizedTest
    @MethodSource("provideComponentsReturningMethods")
    public void componentsReturningMethodsShouldReturnComponentsWithEitherTestOutcomeWhenFilteringBy2TestOutcomes(ComponentsReturningMethod methodWrapper) {
        // Given
        Component component1 = Component.builder()
                .id("test-component-id-1")
                .testResults(List.of(
                        new TestResult("test-id-1", TestOutcome.FAIL, null, null)))
                .build();
        Component component2 = Component.builder()
                .id("test-component-id-2")
                .testResults(List.of(
                        new TestResult("test-id-2", TestOutcome.PASS, null, null)))
                .build();
        Component component3 = Component.builder()
                .id("test-component-id-3")
                .testResults(List.of(
                        new TestResult("test-id-3", TestOutcome.NOT_APPLICABLE, null, null)))
                .build();
        List<Component> components = List.of(component1, component2, component3);

        // When
        List<Component> returnValue = methodWrapper.call(underTest, mockComponentRepository, components, List.of(TestOutcome.FAIL, TestOutcome.PASS));

        // Then
        assertThat(returnValue).containsExactly(component1, component2);
    }

    @Test
    public void getScannersShouldReturnTheScanners() {
        // Given
        List<Scanner> scanners = List.of(Scanner.builder().build());
        when(mockComponentRepository.getScanners()).thenReturn(scanners);

        // When
        List<Scanner> returnValue = underTest.getScanners();

        // Then
        assertThat(returnValue).isSameAs(scanners);
    }

    @Test
    public void getScannerShouldReturnAScanner() {
        // Given
        Scanner scanner = Scanner.builder().id("test-scanner-1").build();
        when(mockComponentRepository.getScanner(scanner.getId())).thenReturn(scanner);

        // When
        Scanner returnValue = underTest.getScanner(scanner.getId());

        // Then
        assertThat(returnValue).isSameAs(scanner);
    }


    @Test
    public void getScannerShouldReturnNullIfNoScannerMatchesId() {
        // Given
        String scannerId = "test-scanner-id";
        when(mockComponentRepository.getScanner(scannerId)).thenReturn(null);

        // When
        Scanner returnValue = underTest.getScanner(scannerId);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getTestsShouldReturnAllTests() {
        // Given
        tech.kronicle.sdk.models.Test test1 = tech.kronicle.sdk.models.Test.builder()
                .id("test-test-id-1")
                .build();
        tech.kronicle.sdk.models.Test test2 = tech.kronicle.sdk.models.Test.builder()
                .id("test-test-id-2")
                .build();
        List<tech.kronicle.sdk.models.Test> tests = List.of(test1, test2);
        when(mockComponentRepository.getTests()).thenReturn(tests);

        // When
        List<tech.kronicle.sdk.models.Test> returnValue = underTest.getTests();

        // Then
        assertThat(returnValue).containsExactly(test1, test2);
    }

    @Test
    public void getTestShouldReturnTheTestWithMatchingId() {
        // Given
        tech.kronicle.sdk.models.Test test1 = tech.kronicle.sdk.models.Test.builder()
                .id("test-test-id")
                .build();
        when(mockComponentRepository.getTest(test1.getId())).thenReturn(test1);

        // When
        tech.kronicle.sdk.models.Test returnValue = underTest.getTest(test1.getId());

        // Then
        assertThat(returnValue).isEqualTo(test1);
    }

    @Test
    public void getTestShouldReturnNullIfNoTestMatchesId() {
        // Given
        String testId = "test-test-id";
        when(mockComponentRepository.getTest(testId)).thenReturn(null);

        // When
        tech.kronicle.sdk.models.Test returnValue = underTest.getTest(testId);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getSummaryShouldReturnTheSummary() {
        // Given
        Summary summary = Summary.EMPTY;
        when(mockComponentRepository.getSummary()).thenReturn(summary);

        // When
        Summary returnValue = underTest.getSummary();

        // Then
        assertThat(returnValue).isSameAs(summary);
    }

    private static Stream<ComponentsReturningMethod> provideComponentsReturningMethods() {
        return Stream.of(
                (underTest, mockComponentRepository, components, testOutcomes) -> {
                    when(mockComponentRepository.getComponents()).thenReturn(components);
                    return underTest.getComponents(Optional.empty(), Optional.empty(), testOutcomes);
                },
                (underTest, mockComponentRepository, components, testOutcomes) -> {
                    when(mockComponentRepository.getTeams()).thenReturn(List.of(createTeam(components)));
                    List<Team> returnValue = underTest.getTeams(testOutcomes);
                    assertThat(returnValue).hasSize(1);
                    return returnValue.get(0).getComponents();
                },
                (underTest, mockComponentRepository, components, testOutcomes) -> {
                    Team team = createTeam(components);
                    when(mockComponentRepository.getTeam(team.getId())).thenReturn(team);
                    Team returnValue = underTest.getTeam(team.getId(), testOutcomes);
                    return returnValue.getComponents();
                },
                (underTest, mockComponentRepository, components, testOutcomes) -> {
                    when(mockComponentRepository.getAreas()).thenReturn(List.of(createArea(components)));
                    List<Area> returnValue = underTest.getAreas(testOutcomes);
                    assertThat(returnValue).hasSize(1);
                    return returnValue.get(0).getComponents();
                },
                (underTest, mockComponentRepository, components, testOutcomes) -> {
                    Area area = createArea(components);
                    when(mockComponentRepository.getArea(area.getId())).thenReturn(area);
                    Area returnValue = underTest.getArea(area.getId(), testOutcomes);
                    return returnValue.getComponents();
                });
    }
    
    private static Team createTeam(List<Component> components) {
        return Team.builder()
                .id("test-team-id")
                .components(components)
                .build();
    }

    private static Area createArea(List<Component> components) {
        return Area.builder()
                .id("test-area-id")
                .components(components)
                .build();
    }

    @FunctionalInterface
    private interface ComponentsReturningMethod {
        
        List<Component> call(ComponentService underTest, ComponentRepository mockComponentRepository, List<Component> components, List<TestOutcome> testOutcomes);
    }

    @Value
    private static class TestComponentState implements ComponentState {

        String type;
        String pluginId = "test-plugin-id";
        String value;

        public TestComponentState(int testComponentStateNumber) {
            this.type = "test-component-state-type-" + testComponentStateNumber;
            this.value = "test-component-value-" + testComponentStateNumber;
        }
    }
}
